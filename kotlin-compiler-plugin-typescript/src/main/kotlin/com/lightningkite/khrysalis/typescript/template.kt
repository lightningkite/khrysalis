package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.*
import com.lightningkite.khrysalis.typescript.replacements.Template
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.AnalysisExtensions
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren


fun <T : Any> PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<T>.dedup(
    requireWrapping: Boolean = false,
    action: DeDupEmitter.() -> Unit
) {
    val emitter = DeDupEmitter(this.partialTranslator as TypescriptTranslator)
    action(emitter)
    val wraps = emitter.dedupNecessary && requireWrapping
    if (wraps) {
        -"(()=>{\n"
    }
    emitter.finish(wraps) {
        -it
    }
    if (wraps) {
        -"\n})()"
    }
}

fun <T : Any> PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<T>.emitTemplate(
    requiresWrapping: Boolean,
    ensureReceiverNotNull: Boolean = false,
    template: Template,
    prefix: Any? = null,
    dispatchReceiver: Any? = null,
    extensionReceiver: Any? = null,
    receiver: Any? = extensionReceiver ?: dispatchReceiver,
    value: Any? = null,
    allParameters: Any? = null,
    operatorToken: Any? = null,
    parameter: (TemplatePart.Parameter) -> Any? = { value },
    typeParameter: (TemplatePart.TypeParameter) -> Any? = { null },
    parameterByIndex: (TemplatePart.ParameterByIndex) -> Any? = { value },
    typeParameterByIndex: (TemplatePart.TypeParameterByIndex) -> Any? = { null }
) {
    dedup(requiresWrapping) {
        val templateIsThisDot = template.parts.getOrNull(0) is TemplatePart.Receiver &&
                template.parts.getOrNull(1).let { it is TemplatePart.Text && it.string.startsWith('.') } &&
                template.parts.none { (it as? TemplatePart.Text)?.string?.contains("=") ?: false }
        val altTemplate = if(ensureReceiverNotNull && templateIsThisDot){
            Template(parts = template.parts.toMutableList().apply {
                this[1] = (this[1] as TemplatePart.Text).let { it.copy("?" + it.string) }
            })
        } else null
        if(ensureReceiverNotNull && altTemplate == null){
            ensureNotNull(extensionReceiver ?: receiver)
        }
        -prefix
        fun onParts(list: List<TemplatePart>) {
            loop@ for (part in list) {
                when (part) {
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> deduplicateEmit(receiver)
                    TemplatePart.DispatchReceiver -> deduplicateEmit(dispatchReceiver)
                    TemplatePart.ExtensionReceiver -> deduplicateEmit(extensionReceiver)
                    TemplatePart.Value -> deduplicateEmit(value)
                    TemplatePart.AllParameters -> -allParameters
                    TemplatePart.OperatorToken -> -operatorToken
                    is TemplatePart.Parameter -> deduplicateEmit(parameter(part))
                    is TemplatePart.ParameterByIndex -> deduplicateEmit(parameterByIndex(part))
                    is TemplatePart.LambdaParameterContents -> {
                        -"\n"
                        val item = when (val p = part.pointer) {
                            is TemplatePart.ParameterByIndex -> parameterByIndex(p)
                            is TemplatePart.Parameter -> parameter(p)
                            else -> continue@loop
                        }
                        if (item is KtLambdaExpression) {
                            part.paramMap
                                .zip(
                                    item.valueParameters
                                        .map { it.name }
                                        .takeUnless { it.isEmpty() }
                                        ?: listOf("it")
                                )
                                .filter { it.second != "_" }
                                .forEach {
                                    -"const "
                                    -it.second
                                    -" = "
                                    onParts(it.first)
                                    -";\n"
                                }
                            -item.bodyExpression
                        } else {
                            -item
                            -"("
                            part.paramMap.forEachBetween(forItem = {
                                onParts(it)
                            }, between = {
                                -", "
                            })
                            -")"
                        }
                        -"\n"
                    }
                    is TemplatePart.TypeParameter -> -typeParameter(part)
                    is TemplatePart.TypeParameterByIndex -> -typeParameterByIndex(part)
                    is TemplatePart.Import -> out.addImport(part)
                }
            }
        }
        onParts(altTemplate?.parts ?: template.parts)
    }
}

fun <T : Any> PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<T>.emitTemplate(
    template: Template,
    receiver: Any? = null,
    dispatchReceiver: Any? = receiver,
    extensionReceiver: Any? = receiver,
    value: Any? = null,
    allParameters: Any? = null,
    operatorToken: Any? = null,
    parameter: (TemplatePart.Parameter) -> Any? = { value },
    typeParameter: (TemplatePart.TypeParameter) -> Any? = { null },
    parameterByIndex: (TemplatePart.ParameterByIndex) -> Any? = { value },
    typeParameterByIndex: (TemplatePart.TypeParameterByIndex) -> Any? = { null }
) {
    for (part in template.parts) {
        when (part) {
            is TemplatePart.Text -> -part.string
            TemplatePart.Receiver -> -receiver
            TemplatePart.DispatchReceiver -> -dispatchReceiver
            TemplatePart.ExtensionReceiver -> -extensionReceiver
            TemplatePart.Value -> -value
            TemplatePart.AllParameters -> -allParameters
            TemplatePart.OperatorToken -> -operatorToken
            is TemplatePart.Parameter -> -parameter(part)
            is TemplatePart.ParameterByIndex -> -parameterByIndex(part)
            is TemplatePart.TypeParameter -> -typeParameter(part)
            is TemplatePart.TypeParameterByIndex -> -typeParameterByIndex(part)
            is TemplatePart.Import -> out.addImport(part)
            is TemplatePart.LambdaParameterContents -> {
            }
        }
    }
}

class DeDupEmitter(analysis: AnalysisExtensions) : AnalysisExtensions by analysis {
    val deduplicated = HashMap<Any, String>()
    val checkNotNull = HashSet<String>()
    val toEmit = ArrayList<Any>()
    fun deduplicate(item: Any) {
        if(deduplicated.containsKey(item)) return
        if (when (item) {
                is String -> item.all { it.isLetterOrDigit() }
                is KtExpression -> item.isSimple()
                else -> false
            }
        ) return
        val name = "temp${uniqueNumber.getAndIncrement()}"
        deduplicated[item] = name
    }

    fun deduplicateEmit(item: Any?) {
        if (item == null) return
        deduplicate(item)
        toEmit.add(item)
    }

    operator fun Any?.unaryMinus() {
        if (this == null) return
        toEmit.add(this)
    }

    operator fun Any?.unaryPlus() {
        if (this == null) return
        toEmit.add(this)
    }

    fun ensureNotNull(item: Any?){
        if(item == null) return
        val name = deduplicated.getOrPut(item) { "temp${uniqueNumber.getAndIncrement()}" }
        checkNotNull.add(name)
    }

    val dedupNecessary: Boolean
        get() = deduplicated.any {
            val key = it.key
            toEmit.count { it == key } > 1
        } || checkNotNull.isNotEmpty()

    fun finish(wrapping: Boolean, parentEmit: (Any) -> Unit) {
        val used = deduplicated.filter { (key, value) ->
            toEmit.count { it == key } > 1 || value in checkNotNull
        }
        for ((item, name) in used) {
            parentEmit("const ")
            parentEmit(name)
            parentEmit(" = ")
            parentEmit(item)
            parentEmit(";\n")
            if(name in checkNotNull){
                if(wrapping){
                    parentEmit("if(")
                    parentEmit(name)
                    parentEmit(" === null) { return null }\n")
                } else {
                    parentEmit("if(")
                    parentEmit(name)
                    parentEmit(" !== null) { \n")
                }
            }
        }
        if(wrapping) {
            parentEmit("return ")
        }
        for (item in toEmit) {
            used[item]?.let { name ->
                parentEmit(name)
            } ?: parentEmit(item)
        }
        if(!wrapping) {
            repeat(checkNotNull.size){
                parentEmit("\n}")
            }
        }
    }
}

fun hasNewlineBeforeAccess(typedRule: KtQualifiedExpression): Boolean {
    return typedRule.allChildren
        .find { it is LeafPsiElement && (it.elementType == KtTokens.DOT || it.elementType == KtTokens.SAFE_ACCESS) }
        ?.prevSibling
        ?.let { it as? PsiWhiteSpace }
        ?.textContains('\n') == true
}

fun <T : KtQualifiedExpression> PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<T>.insertNewlineBeforeAccess() {
    if (hasNewlineBeforeAccess(typedRule)) {
        -"\n"
    }
}
package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.*
import com.lightningkite.khrysalis.replacements.Template
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport


fun <T : Any> KotlinTranslator<TypescriptFileEmitter>.ContextByType<T>.dedup(
    requireWrapping: Boolean = false,
    type: Any? = null,
    cannotDedup: Boolean = false,
    action: DeDupEmitter.() -> Unit
) {
    val emitter = DeDupEmitter(out)
    if(cannotDedup){
        action(emitter)
        -emitter.toEmit
        return
    }
    action(emitter)
    val wraps = emitter.dedupNecessary && requireWrapping
    if (wraps) {
        -"(()"
        if(type != null){
            -": "
            -type
        }
        -" => {\n"
    }
    emitter.finish(wraps) {
        -it
    }
    if (wraps) {
        -"\n})()"
    }
}

fun <T : Any> KotlinTranslator<TypescriptFileEmitter>.ContextByType<T>.emitTemplate(
    requiresWrapping: Boolean,
    type: Any? = null,
    ensureReceiverNotNull: Boolean = false,
    template: Template,
    prefix: Any? = null,
    dispatchReceiver: Any? = null,
    extensionReceiver: Any? = null,
    receiver: Any? = extensionReceiver ?: dispatchReceiver,
    value: Any? = null,
    allParameters: () -> Any? = { null },
    operatorToken: Any? = null,
    parameter: (TemplatePart.Parameter) -> Any? = { value },
    typeParameter: (TemplatePart.TypeParameter) -> Any? = { null },
    parameterByIndex: (TemplatePart.ParameterByIndex) -> Any? = { value },
    typeParameterByIndex: (TemplatePart.TypeParameterByIndex) -> Any? = { null },
    reifiedTypeParameterByIndex: (TemplatePart.ReifiedTypeParameterByIndex) -> Any? = { null }
) {
    val replacements = template.imports.mapNotNull { out.addImport(it) }
    dedup(requiresWrapping, type) {
//        val templateIsThisDot = template.parts.getOrNull(0) is TemplatePart.Receiver &&
//                template.parts.getOrNull(1).let { it is TemplatePart.Text && it.string.startsWith('.') } &&
//                template.parts.none { (it as? TemplatePart.Text)?.string?.contains("=") ?: false }
//        val altTemplate = if(ensureReceiverNotNull && templateIsThisDot){
//            Template(parts = template.parts.toMutableList().apply {
//                this[1] = (this[1] as TemplatePart.Text).let { it.copy("?" + it.string) }
//            })
//        } else null
        val templateIsThisDot = false
        val altTemplate: Template? = null
        if(ensureReceiverNotNull && altTemplate == null){
            ensureNotNull(extensionReceiver ?: receiver)
        }
        -prefix
        fun onParts(list: List<TemplatePart>, overridden: Map<String, Any?> = mapOf()) {
            loop@ for (part in list) {
                when (part) {
                    is TemplatePart.Text -> -replacements.fold(part.string) { r, t -> r.replace(t.from, t.to) }
                    TemplatePart.Receiver -> deduplicateEmit(receiver)
                    TemplatePart.DispatchReceiver -> deduplicateEmit(dispatchReceiver)
                    TemplatePart.ExtensionReceiver -> deduplicateEmit(extensionReceiver)
                    TemplatePart.Value -> deduplicateEmit(value)
                    TemplatePart.AllParameters -> -allParameters()
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
                                    onParts(it.first.parts)
                                    -";\n"
                                }
                            -item.bodyExpression
                        } else {
                            -item
                            -"("
                            part.paramMap.forEachBetween(forItem = {
                                onParts(it.parts)
                            }, between = {
                                -", "
                            })
                            -")"
                        }
                        -"\n"
                    }
                    is TemplatePart.TypeParameter -> -typeParameter(part)
                    is TemplatePart.TypeParameterByIndex -> -typeParameterByIndex(part)
                    is TemplatePart.ReifiedTypeParameterByIndex -> -reifiedTypeParameterByIndex(part)
                }
            }
        }
        onParts(altTemplate?.parts ?: template.parts)
    }
}

fun <T : Any> KotlinTranslator<TypescriptFileEmitter>.ContextByType<T>.emitTemplate(
    template: Template,
    receiver: Any? = null,
    dispatchReceiver: Any? = receiver,
    extensionReceiver: Any? = receiver,
    value: Any? = null,
    allParameters: () -> Any? = { null },
    operatorToken: Any? = null,
    parameter: (TemplatePart.Parameter) -> Any? = { value },
    typeParameter: (TemplatePart.TypeParameter) -> Any? = { null },
    parameterByIndex: (TemplatePart.ParameterByIndex) -> Any? = { value },
    typeParameterByIndex: (TemplatePart.TypeParameterByIndex) -> Any? = { null },
    reifiedTypeParameterByIndex: (TemplatePart.ReifiedTypeParameterByIndex) -> Any? = { null }
) {
    emitTemplate(
        requiresWrapping = false,
        type = null,
        ensureReceiverNotNull = false,
        template = template,
        prefix = null,
        dispatchReceiver = dispatchReceiver,
        extensionReceiver = extensionReceiver,
        receiver = receiver,
        value = value,
        allParameters = allParameters,
        operatorToken = operatorToken,
        parameter = parameter,
        typeParameter = typeParameter,
        parameterByIndex = parameterByIndex,
        typeParameterByIndex = typeParameterByIndex,
        reifiedTypeParameterByIndex = reifiedTypeParameterByIndex
    )
}

class DeDupEmitter(val out: TypescriptFileEmitter) {
    val deduplicated = HashMap<Any, String>()
    val checkNotNull = HashSet<Any>()
    val toEmit = ArrayList<Any>()
    private fun Any?.isSimpleEnough(): Boolean = when (this) {
        is String -> this.all { it.isLetterOrDigit() }
        is KtExpression -> this.isSimple()
        is List<*> -> this.all { it.isSimpleEnough() }
        else -> false
    }
    fun deduplicate(item: Any) {
        if(deduplicated.containsKey(item)) return
        if (item.isSimpleEnough()) return
        val name = "temp${out.uniqueNumber.getAndIncrement()}"
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
        if(item.isSimpleEnough()) {
            checkNotNull.add(item)
        } else {
            val name = deduplicated.getOrPut(item) { "temp${out.uniqueNumber.getAndIncrement()}" }
            checkNotNull.add(name)
        }
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
        }
        for(item in checkNotNull){
            if(wrapping){
                parentEmit("if (")
                parentEmit(item)
                parentEmit(" === null) { return null }\n")
            } else {
                parentEmit("if (")
                parentEmit(item)
                parentEmit(" !== null) { \n")
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

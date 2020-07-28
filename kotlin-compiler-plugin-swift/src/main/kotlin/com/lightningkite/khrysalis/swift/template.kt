package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.generic.*
import com.lightningkite.khrysalis.swift.replacements.Template
import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import com.lightningkite.khrysalis.util.AnalysisExtensions
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*


fun <T : Any> PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<T>.dedup(
    requireWrapping: Boolean = false,
    cannotDedup: Boolean = false,
    action: DeDupEmitter.() -> Unit
) {
    val emitter = DeDupEmitter(this.partialTranslator as SwiftTranslator)
    if(cannotDedup){
        action(emitter)
        -emitter.toEmit
        return
    }
    action(emitter)
    val dedup = emitter.dedupNecessary
    if (dedup && requireWrapping) {
        -"run {"
    }
    emitter.finish { -it }
    if (dedup && requireWrapping) {
        -"}"
    }
}

fun <T : Any> PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<T>.emitTemplate(
    requiresWrapping: Boolean,
    cannotDedup: Boolean = false,
    template: Template,
    prefix: Any? = null,
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
    dedup(requiresWrapping, cannotDedup) {
        -prefix
        fun onParts(list: List<TemplatePart>, overridden: Map<String, Any?> = mapOf()) {
            fun getRaw(part: TemplatePart): String? = when (part) {
                is TemplatePart.Text -> part.string
                TemplatePart.Receiver -> receiver
                TemplatePart.DispatchReceiver -> dispatchReceiver
                TemplatePart.ExtensionReceiver -> extensionReceiver
                TemplatePart.Value -> value
                TemplatePart.AllParameters -> allParameters
                TemplatePart.OperatorToken -> operatorToken
                is TemplatePart.Parameter -> parameter(part)
                is TemplatePart.TypeParameter -> typeParameter(part)
                is TemplatePart.LambdaParameterContents -> null
                is TemplatePart.ParameterByIndex -> parameterByIndex(part)
                is TemplatePart.TypeParameterByIndex -> typeParameterByIndex(part)
                is TemplatePart.Import -> null
                is TemplatePart.Switch -> null
                is TemplatePart.Split -> null
            }?.let {
                when (it) {
                    is PsiElement -> it.text
                    is String -> it
                    else -> null
                }
            }
            loop@ for (part in list) {
                when (part) {
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> deduplicateEmit(receiver)
                    TemplatePart.DispatchReceiver -> deduplicateEmit(dispatchReceiver)
                    TemplatePart.ExtensionReceiver -> deduplicateEmit(extensionReceiver)
                    TemplatePart.Value -> deduplicateEmit(value)
                    TemplatePart.AllParameters -> -allParameters
                    TemplatePart.OperatorToken -> -operatorToken
                    is TemplatePart.Parameter -> deduplicateEmit(overridden[part.name] ?: parameter(part))
                    is TemplatePart.ParameterByIndex -> deduplicateEmit(parameterByIndex(part))
                    is TemplatePart.LambdaParameterContents -> {
                        -"\n"
                        val item = when (val p = part.pointer) {
                            is TemplatePart.ParameterByIndex -> parameterByIndex(p)
                            is TemplatePart.Parameter -> parameter(p)
                            else -> continue@loop
                        }
                        if (item is KtLambdaExpression) {
                            part.paramMap.zip(item.valueParameters.map { it.name }.takeUnless { it.isEmpty() }
                                ?: listOf("it")).forEach {
                                -"let "
                                -it.second
                                -" = "
                                onParts(it.first.parts)
                                -"\n"
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
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Split -> {
                        getRaw(part.on)?.let {
                            part.before?.let { onParts(listOf(it), overridden) }
                            it.splitToSequence(part.by)
                                .forEachBetween(
                                    forItem = {
                                        onParts(part.each.parts, overridden + (part.name to it))
                                    },
                                    between = { part.between?.let { onParts(listOf(it), overridden) } }
                                )
                            part.after?.let { onParts(listOf(it), overridden) }
                        }
                    }
                    is TemplatePart.Switch -> {
                        (part.cases[getRaw(part.on)?.trim()] ?: part.cases["default"])?.let {
                            onParts(it.parts, overridden + (part.name to it))
                        }
                    }
                }
            }
        }
        onParts(template.parts)
    }
}

fun <T : Any> PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<T>.emitTemplate(
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
    emitTemplate(
        requiresWrapping = false,
        cannotDedup = true,
        template = template,
        receiver = receiver,
        dispatchReceiver = dispatchReceiver,
        extensionReceiver = extensionReceiver,
        value = value,
        allParameters = allParameters,
        operatorToken = operatorToken,
        parameter = parameter,
        typeParameter = typeParameter,
        parameterByIndex = parameterByIndex,
        typeParameterByIndex = typeParameterByIndex
    )
}

class DeDupEmitter(val swift: SwiftTranslator) {
    val deduplicated = HashMap<Any, String>()
    val toEmit = ArrayList<Any>()
    fun deduplicate(item: Any) {
        if (when (item) {
                is String -> item.all { it.isLetterOrDigit() }
                is KtExpression -> with(swift) { item.isSimple() }
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

    val dedupNecessary: Boolean
        get() = deduplicated.any {
            val key = it.key
            toEmit.count { it == key } > 1
        }

    fun finish(parentEmit: (Any) -> Unit) = with(swift) {
        val used = deduplicated.filterKeys { key ->
            toEmit.count { it == key } > 1
        }
        for ((item, name) in used) {
//            println("Deduping ${(item as? KtExpression)?.text ?: item.toString()}, which is of type ${(item as? KtExpression)?.resolvedExpressionTypeInfo?.type}")
            if ((item as? KtExpression)?.resolvedExpressionTypeInfo?.type?.requiresMutable() == true) {
                parentEmit("var ")
            } else {
                parentEmit("let ")
            }
            parentEmit(name)
            parentEmit(" = ")
            parentEmit(item)
            parentEmit("\n")
        }
        for (item in toEmit) {
            used[item]?.let { name ->
                parentEmit(name)
            } ?: parentEmit(item)
        }
        for ((item, name) in used.filter { (it.key as? KtExpression)?.resolvedExpressionTypeInfo?.type?.requiresMutable() == true }) {
            parentEmit(item)
            parentEmit(" = ")
            parentEmit(name)
            parentEmit("\n")
        }
    }
}
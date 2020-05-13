package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.*
import com.lightningkite.khrysalis.typescript.replacements.Template
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression


fun <T : Any> PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<T>.dedup(
    requireWrapping: Boolean = false,
    action: DeDupEmitter.() -> Unit
)  {
    val emitter = DeDupEmitter()
    action(emitter)
    val dedup = emitter.dedupNecessary
    if (dedup && requireWrapping) {
        -"(()=>{"
    }
    emitter.finish { -it }
    if (dedup && requireWrapping) {
        -"})()"
    }
}

fun <T : Any> PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<T>.emitTemplate(
    requiresWrapping: Boolean,
    template: Template,
    prefix: Any? = null,
    receiver: Any? = null,
    dispatchReceiver: Any? = receiver,
    extensionReceiver: Any? = receiver,
    value: Any? = null,
    allParameters: Any? = null,
    operatorToken: Any? = null,
    parameter: (TemplatePart.Parameter) -> Any? = { null },
    typeParameter: (TemplatePart.TypeParameter) -> Any? = { null },
    parameterByIndex: (TemplatePart.ParameterByIndex) -> Any? = { null },
    typeParameterByIndex: (TemplatePart.TypeParameterByIndex) -> Any? = { null }
) {
    dedup(requiresWrapping) {
        -prefix
        for (part in template.parts) {
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
                is TemplatePart.TypeParameter -> -typeParameter(part)
                is TemplatePart.TypeParameterByIndex -> -typeParameterByIndex(part)
                is TemplatePart.Import -> out.addImport(part)
            }
        }
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
    parameter: (TemplatePart.Parameter) -> Any? = { null },
    typeParameter: (TemplatePart.TypeParameter) -> Any? = { null },
    parameterByIndex: (TemplatePart.ParameterByIndex) -> Any? = { null },
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
        }
    }
}

class DeDupEmitter() {
    val deduplicated = HashMap<Any, String>()
    val toEmit = ArrayList<Any>()
    fun deduplicate(item: Any) {
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

    val dedupNecessary: Boolean
        get() = deduplicated.any {
            val key = it.key
            toEmit.count { it == key } > 1
        }

    fun finish(parentEmit: (Any) -> Unit) {
        val used = deduplicated.filterKeys { key ->
            toEmit.count { it == key } > 1
        }
        for ((item, name) in used) {
            parentEmit("const ")
            parentEmit(name)
            parentEmit(" = ")
            parentEmit(item)
            parentEmit(";\n")
        }
        for (item in toEmit) {
            used[item]?.let { name ->
                parentEmit(name)
            } ?: parentEmit(item)
        }
    }
}
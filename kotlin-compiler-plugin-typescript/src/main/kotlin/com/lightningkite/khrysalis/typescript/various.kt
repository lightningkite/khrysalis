package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import com.lightningkite.khrysalis.analysis.*
import org.jetbrains.kotlin.types.KotlinType

fun List<Any?>.withBetween(separator: Any?, start: Any? = null, end: Any? = null): List<Any?> {
    if(this.isEmpty()) return emptyList()
    val list = ArrayList<Any?>(this.size * 2 - 1 + if (start != null) 1 else 0 + if (end != null) 1 else 0)
    if (start != null) list.add(start)
    this.forEachBetween(
        forItem = { list.add(it) },
        between = { list.add(separator) }
    )
    if (end != null) list.add(end)
    return list
}


val ResolvedCall<out CallableDescriptor>.template_allParameter: () -> List<Any?>
    get() {
        return {  ->
            this.resultingDescriptor.valueParameters.map {
                this.valueArguments[it]
            }.map {
                it?.arguments?.map { it.getArgumentExpression() }?.withBetween(", ")?.takeUnless { it.isEmpty() } ?: "undefined"
            }.withBetween(separator = ", ")
        }
    }
val ResolvedCall<out CallableDescriptor>.template_parameter
    get() = { n: TemplatePart.Parameter ->
        this.valueArguments.entries.find { it.key.name.asString() == n.name }?.let {
            if (it.key.isVararg)
                it.value.arguments.map { it.getArgumentExpression() }.withBetween(", ") as Any?
            else
                it.value.arguments.firstOrNull()?.getArgumentExpression() as Any?
        } ?: "undefined"
    }
val ResolvedCall<out CallableDescriptor>.template_typeParameter get() = { n: TemplatePart.TypeParameter -> this.typeArguments.entries.find { it.key.name.asString() == n.name }?.value }
val ResolvedCall<out CallableDescriptor>.template_parameterByIndex
    get() = { n: TemplatePart.ParameterByIndex ->
        this.valueArguments.entries.find { it.key.index == n.index }?.let {
            if (it.key.isVararg)
                it.value.arguments.map { it.getArgumentExpression() }.withBetween(", ") as Any?
            else
                it.value.arguments.firstOrNull()?.getArgumentExpression() as Any?
        } ?: "undefined"
    }
val ResolvedCall<out CallableDescriptor>.template_typeParameterByIndex get() = { n: TemplatePart.TypeParameterByIndex -> this.typeArguments.entries.find { it.key.index == n.index }?.value }
val ResolvedCall<out CallableDescriptor>.template_reifiedTypeParameterByIndex get() = { n: TemplatePart.ReifiedTypeParameterByIndex ->
    this.typeArguments.entries.find { it.key.index == n.index }?.value?.let { CompleteReflectableType(it) }
}
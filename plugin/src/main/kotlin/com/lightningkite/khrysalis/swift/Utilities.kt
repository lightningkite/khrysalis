package com.lightningkite.khrysalis.swift

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser
import java.lang.IllegalStateException


inline fun <reified T> RuleContext.parentIfType() = (parent as? T)
inline fun <reified T: ParserRuleContext> RuleContext.parentIfTypeAndOnlyChildOfType() = (parent as? T)?.takeIf {
    it.children.count { it::class == this::class } == 1
}
inline fun <reified T: ParserRuleContext> RuleContext.parentIfTypeAndOnlyChildOfTypeOrThis() = (parent as? T)?.takeIf {
    it.children.count { it::class == this::class } == 1
} ?: this

inline fun <reified T> RuleContext.parentOfType() = parentOfType(T::class.java)
tailrec fun <T> RuleContext.parentOfType(type: Class<T>): T? {
    if (parent == null) return null
    else return parent.parentOfType(type)
}


fun KotlinParser.TypeContext.getParentAnnotationTargetTypeContext(): KotlinParser.TypeContext {
    this.parentIfType<KotlinParser.ParenthesizedTypeContext>()
        ?.parentIfType<KotlinParser.TypeContext>()
        ?.let { return it.getParentAnnotationTargetTypeContext() }
    this.parentIfType<KotlinParser.NullableTypeContext>()
        ?.parentIfType<KotlinParser.ParenthesizedTypeContext>()
        ?.parentIfType<KotlinParser.TypeContext>()
        ?.let { return it.getParentAnnotationTargetTypeContext() }
    return this
}

fun KotlinParser.TypeContext.getUnderlyingType(): KotlinParser.TypeContext {
    this.parenthesizedType()?.type()?.let{ return it.getUnderlyingType() }
    this.nullableType()?.parenthesizedType()?.type()?.let { return it.getUnderlyingType() }
    return this
}


fun KotlinParser.ReceiverTypeContext.getUserType(): KotlinParser.UserTypeContext {
    this.nullableType()?.let { throw IllegalStateException() }
    this.parenthesizedType()?.type()?.let { return it.getUserType() }
    this.typeReference()?.let { return it.getUserType() }
    throw IllegalStateException()
}

private fun KotlinParser.TypeContext.getUserType(): KotlinParser.UserTypeContext {
    this.nullableType()?.let { throw IllegalStateException() }
    this.parenthesizedType()?.type()?.let { return it.getUserType() }
    this.typeReference()?.let { return it.getUserType() }
    throw IllegalStateException()
}

private fun KotlinParser.TypeReferenceContext.getUserType(): KotlinParser.UserTypeContext {
    return this.userType() ?: throw IllegalStateException()
}


fun ParserRuleContext.usedAsStatement(): Boolean {
    return this
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.PrimaryExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.PostfixUnaryExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.PrefixUnaryExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.AsExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.MultiplicativeExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.AdditiveExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.RangeExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.InfixFunctionCallContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.ElvisExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.InfixOperationContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.ComparisonContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.EqualityContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.ConjunctionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.DisjunctionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.ExpressionContext>()
        .parentIfTypeAndOnlyChildOfTypeOrThis<KotlinParser.StatementContext>() is KotlinParser.StatementContext
}


fun <E> List<E>.oneOnly(): E? = if (this.size == 1) first() else null

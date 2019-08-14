package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerVariable() {
    handle<KotlinParser.PropertyDeclarationContext> { item ->
        val owningClass = item.parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
            ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
            ?.parentIfType<KotlinParser.ClassBodyContext>()
            ?.parentIfType<KotlinParser.ClassDeclarationContext>()
        val myName = item.variableDeclaration().simpleIdentifier().text
        val originalUsesOverride =
            item.modifiers()?.modifier()?.any { it.memberModifier()?.OVERRIDE() != null } ?: false
        val needsOverrideKeyword = originalUsesOverride && owningClass?.implements()
            ?.any { myName in it.properties } != true

        var initialSetExpression = item.expression()
        var useWeak = false
        item.propertyDelegate()?.expression()?.let {
            val x = it.disjunction()
                ?.conjunction()?.oneOnly()
                ?.equality()?.oneOnly()
                ?.comparison()?.oneOnly()
                ?.infixOperation()?.oneOnly()
                ?.elvisExpression()?.oneOnly()
                ?.infixFunctionCall()?.oneOnly()
                ?.rangeExpression()?.oneOnly()
                ?.additiveExpression()?.oneOnly()
                ?.multiplicativeExpression()?.oneOnly()
                ?.asExpression()?.oneOnly()
                ?.prefixUnaryExpression()
                ?.postfixUnaryExpression()
            if(x?.primaryExpression()?.text == "weak"){
                initialSetExpression = x.postfixUnarySuffix()?.oneOnly()?.callSuffix()?.valueArguments()?.valueArgument(0)?.expression()
                useWeak = true
            }
        }

        with(direct) {
            if (needsOverrideKeyword) {
                append("override ")
            }
            if (owningClass != null) {
                append(item.modifiers().visibilityString())
                append(" ")
            }
            if (useWeak) {
                append("weak ")
            }
            append("var ")
            append(item.variableDeclaration().simpleIdentifier().text)
            item.variableDeclaration().type()?.let {
                append(": ")
                write(it)
            }
            initialSetExpression?.let {
                append(" = ")
                write(it)
            }
            if (owningClass?.INTERFACE() != null) {
                if (item.VAL() != null) {
                    append(" { get }")
                } else {
                    append(" { get set }")
                }
            }
        }
    }
}

inline fun <reified T> RuleContext.parentIfType() = (parent as? T)

inline fun <reified T> RuleContext.parentOfType() = parentOfType(T::class.java)
tailrec fun <T> RuleContext.parentOfType(type: Class<T>): T? {
    if (parent == null) return null
    else return parent.parentOfType(type)
}

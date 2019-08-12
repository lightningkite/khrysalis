package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerVariable() {
    handle<KotlinParser.PropertyDeclarationContext> { item ->
        val owningClass = item.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
            ?.parentIfType<KotlinParser.ClassBodyContext>()
            ?.parentIfType<KotlinParser.ClassDeclarationContext>()
        val myName = item.variableDeclaration().simpleIdentifier().text
        val originalUsesOverride = item.modifierList()?.modifier()?.any{ it.memberModifier()?.OVERRIDE() != null } ?: false
        val needsOverrideKeyword = originalUsesOverride && owningClass?.implements()
            ?.any { myName in it.properties } != true

        with(direct) {
            if(needsOverrideKeyword){
                append("override ")
            }
            if(owningClass != null){
                append(item.modifierList().visibilityString())
                append(" ")
            }
            append("var ")
            append(item.variableDeclaration().simpleIdentifier().text)
            item.variableDeclaration().type()?.let {
                append(": ")
                append(it.toSwift())
            }
            item.expression()?.let {
                append(" = ")
                write(it)
            }
            if(owningClass?.INTERFACE() != null){
                if(item.VAL() != null) {
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
    if(parent == null) return null
    else return parent.parentOfType(type)
}

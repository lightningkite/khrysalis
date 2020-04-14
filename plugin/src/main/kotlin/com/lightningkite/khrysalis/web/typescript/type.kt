package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.emitDefault
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.actuals.visibility
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun KotlinParser.TypeContext.basic(): KotlinParser.SimpleUserTypeContext? = this.typeReference()?.userType()?.simpleUserType(0)
    ?: this.nullableType()?.let {
        it.parenthesizedType()?.type()?.basic() ?: it.typeReference()?.userType()?.simpleUserType(0)
    } ?: this.parenthesizedType()?.type()?.basic()

val primitiveTypes = setOf("Number", "String", "Boolean", "Unit", "Any" )

fun KotlinParser.TypeContext.isPrimitive(): Boolean = this.typeReference()?.userType()?.simpleUserType(0)?.let { it.text in primitiveTypes }
    ?: this.nullableType()?.let {
        it.parenthesizedType()?.type()?.isPrimitive() ?: it.typeReference()?.userType()?.simpleUserType(0)?.let { it.text in primitiveTypes }
    } ?: this.parenthesizedType()?.type()?.isPrimitive()
    ?: false

fun TypescriptTranslator.registerType(){

    handle<KotlinParser.TypeAliasContext> {
        val rule = typedRule
        val item = rule
        val name = item.simpleIdentifier().text
        line {
            if(item.visibility().isExposed){
                -("export ")
            }
            -("type ")
            -(name)
            item.typeParameters()?.let {
                write(it)
            }
            -(" = ")
            write(item.type())
            -(';')
        }
        line {
            if(item.visibility().isExposed){
                -("export ")
            }
            -("let ")
            -(name)
            -(" = ")
            val b = item.type().basic()
            when(val t = buildString { translate(b!!, this) }.trim().substringBefore("<")){
                in primitiveTypes -> -t.capitalize()
                null -> throw IllegalArgumentException("Cannot do a typealias to a function type")
                else -> -t
            }
            -(';')
        }
    }
    handle<KotlinParser.FunctionTypeContext> {
        val rule = typedRule
        -"("
        rule.functionTypeParameters().parameter().forEachBetween(
            forItem = { -it },
            between = { -", " }
        )
        -") => "
        -rule.type()
    }
    handle<KotlinParser.UserTypeContext> { emitDefault(rule, "") }
    handle<KotlinParser.NullableTypeContext> {
        val rule = typedRule
        -(rule.typeReference() ?: rule.parenthesizedType())
        -" | null"
    }
    handle<KotlinParser.SimpleUserTypeContext> {
        val rule = typedRule
        val name = typeReplacements[rule.simpleIdentifier().text] ?: rule.simpleIdentifier().text
        if(name.endsWith("*")){
            -name.removeSuffix("*")
            return@handle
        }
        -name
        -rule.typeArguments()
    }
    handle<KotlinParser.TypeArgumentsContext> {
        val rule = typedRule
        -"<"
        rule.typeProjection().forEachBetween(
            forItem = { -it.type() },
            between = { -", " }
        )
        -">"
    }

    handle<KotlinParser.ParenthesizedTypeContext> { emitDefault(rule, "") }
    handle<KotlinParser.ParenthesizedUserTypeContext> { emitDefault(rule, "") }
}

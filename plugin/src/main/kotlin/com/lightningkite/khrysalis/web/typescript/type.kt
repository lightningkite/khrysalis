package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.ios.swift.actuals.visibility
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun KotlinParser.TypeContext.basic(): KotlinParser.SimpleUserTypeContext? = this.typeReference()?.userType()?.simpleUserType(0)
    ?: this.nullableType()?.let {
        it.parenthesizedType()?.type()?.basic() ?: it.typeReference()?.userType()?.simpleUserType(0)
    } ?: this.parenthesizedType()?.type()?.basic()


fun TypescriptTranslator.registerType(){

    handle<KotlinParser.TypeAliasContext> {
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
            when(val t = buildString { invoke(this, b!!) }.trim().substringBefore("<")){
                "number", "string", "boolean", "void", "any" -> -t.capitalize()
                null -> throw IllegalArgumentException("Cannot do a typealias to a function type")
                else -> -t
            }
            -(';')
        }
    }
    handle<KotlinParser.FunctionTypeContext> {
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
        -(rule.typeReference() ?: rule.parenthesizedType())
        -" | null"
    }
    handle<KotlinParser.SimpleUserTypeContext> {
        val name = typeReplacements[rule.simpleIdentifier().text] ?: rule.simpleIdentifier().text
        if(name.endsWith("*")){
            -name.removeSuffix("*")
            return@handle
        }
        -name
        -rule.typeArguments()
    }
    handle<KotlinParser.TypeArgumentsContext> {
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

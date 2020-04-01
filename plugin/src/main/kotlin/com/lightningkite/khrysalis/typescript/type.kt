package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.TabWriter
import com.lightningkite.khrysalis.swift.actuals.visibility
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerType(){

    handle<KotlinParser.TypeAliasContext> { item ->
        val name = item.simpleIdentifier().text
        line {
            if(item.visibility().isExposed){
                append("export ")
            }
            append("type ")
            append(name)
            item.typeParameters()?.let {
                write(it)
            }
            append(" = ")
            write(item.type())
            append(';')
        }
        line {
            if(item.visibility().isExposed){
                append("export ")
            }
            append("let ")
            append(name)
            append(" = ")
            val b = item.type().getBasicType()
            if(b.startsWith('*')) {
                append(b.drop(1).capitalize())
            } else {
                append(b)
            }
            append(';')
        }
    }
    handle<KotlinParser.FunctionTypeContext> { item ->

    }
    handle<KotlinParser.UserTypeContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.NullableTypeContext> { item ->
        write(item.typeReference() ?: item.parenthesizedType())
        direct.append(" | null")
    }
    handle<KotlinParser.SimpleUserTypeContext> { item ->
        val name = typeReplacements[item.simpleIdentifier().text] ?: item.simpleIdentifier().text
        if(name.endsWith("*")){
            direct.append(name.removeSuffix("*"))
            return@handle
        }
        direct.append(name)
        item.typeArguments()?.let {
            direct.append("<")
            it.typeProjection().forEachBetween(
                forItem = {
                    it.type()?.let { write(it) } ?: run {
                        direct.append("*")
                    }
                },
                between = {
                    direct.append(", ")
                }
            )
            direct.append(">")
        }
    }

    handle<KotlinParser.ParenthesizedTypeContext> { item -> defaultWrite(item, "") }
    handle<KotlinParser.ParenthesizedUserTypeContext> { item -> defaultWrite(item, "") }
}

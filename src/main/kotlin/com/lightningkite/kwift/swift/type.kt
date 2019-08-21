package com.lightningkite.kwift.swift

import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerType() {
    handle<KotlinParser.TypeAliasContext> { item ->
        line {
            append(item.modifiers().visibilityString())
            append(" typealias ")
            append(item.simpleIdentifier().text)
            item.typeParameters()?.let { writeTypeArguments(this@handle, it.typeParameter()) }
            append(" = ")
            write(item.type())
        }
    }
    handle<KotlinParser.SimpleUserTypeContext> { item ->
        val name = typeReplacements[item.simpleIdentifier().text] ?: item.simpleIdentifier().text
        direct.append(name)
        item.typeArguments()?.let {
            direct.append("<")
            it.typeProjection().forEachBetween(
                forItem = {
                    write(it.type())
                },
                between = {
                    direct.append(", ")
                }
            )
            direct.append(">")
        }
    }
    handle<KotlinParser.FunctionTypeParametersContext> { item ->
        direct.append('(')
        item.children.asSequence()
            .filter { it is KotlinParser.TypeContext || it is KotlinParser.ParameterContext }
            .forEachBetween(
                forItem = { write(it as ParserRuleContext) },
                between = { direct.append(", ") }
            )
        direct.append(')')
    }
    handle<KotlinParser.ParenthesizedTypeContext> { item -> defaultWrite(item, "") }
    handle<KotlinParser.ParenthesizedUserTypeContext> { item -> defaultWrite(item, "") }
    handle<KotlinParser.SingleAnnotationContext> { item ->
        if (item.unescapedAnnotation().text.startsWith("escaping")) {
            if (filterEscapingAnnotation) {
                return@handle
            }
        }
        direct.append('@')
        write(item.unescapedAnnotation())
    }
    handle<KotlinParser.UnescapedAnnotationContext> {
        it.constructorInvocation()?.let {
            write(it.userType())
        }
        it.userType()?.let {
            write(it)
        }
    }
    handle<KotlinParser.NullableTypeContext> { defaultWrite(it, "") }
}

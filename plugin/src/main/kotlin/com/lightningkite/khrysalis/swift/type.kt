package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerType() {
    handle<KotlinParser.TypeAliasContext> { item ->
        line {
            append(item.modifiers().visibilityString())
            append(" typealias ")
            append(item.simpleIdentifier().text)
            item.typeParameters()?.let { writeTypeArguments(this@handle, it.typeParameter(), null) }
            append(" = ")
            write(item.type())
        }
    }
    handle<KotlinParser.UserTypeContext> { item ->
        defaultWrite(item, "")
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
    handle<KotlinParser.FunctionTypeParametersContext> { item ->
        direct.append('(')
        item.children.asSequence()
            .filter { it is KotlinParser.TypeContext || it is KotlinParser.ParameterContext }
            .forEachBetween(
                forItem = {
                    if(it is KotlinParser.ParameterContext) {
                        direct.append("_ ")
                        write(it)
                    } else write(it as ParserRuleContext)
                },
                between = { direct.append(", ") }
            )
        direct.append(')')
    }
    handle<KotlinParser.ParenthesizedTypeContext> { item -> defaultWrite(item, "") }
    handle<KotlinParser.ParenthesizedUserTypeContext> { item -> defaultWrite(item, "") }
    handle<KotlinParser.AnnotationContext> { item ->
        item.children?.filter { it is ParserRuleContext }?.forEachBetween(
            forItem = { child ->
                when (child) {
                    is ParserRuleContext -> write(child)
                }
            },
            between = { direct.append(" ") }
        )
    }
    handle<KotlinParser.SingleAnnotationContext> { item ->
        val unescapedText = item.unescapedAnnotation().text
        when {
            unescapedText.startsWith("escaping") && (filterEscapingAnnotation && item.isOnTopLevelParameter()) -> return@handle
            unescapedText.startsWith("swift") -> return@handle
            unescapedText.startsWith("unowned") -> return@handle
            unescapedText.startsWith("unownedSelf") -> return@handle
            unescapedText.startsWith("weakSelf") -> return@handle
            unescapedText.startsWith("Deprecated") -> return@handle
            unescapedText.startsWith("JvmName") -> return@handle
            unescapedText.startsWith("Suppress") -> return@handle
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

fun KotlinParser.SingleAnnotationContext.isOnTopLevelParameter(): Boolean {
    val x = this.parentIfType<KotlinParser.AnnotationContext>()
        ?.parentIfType<KotlinParser.TypeModifierContext>()
        ?.parentIfType<KotlinParser.TypeModifiersContext>()
        ?.parentIfType<KotlinParser.TypeContext>()
    return x?.parentIfType<KotlinParser.ClassParameterContext>() != null
}

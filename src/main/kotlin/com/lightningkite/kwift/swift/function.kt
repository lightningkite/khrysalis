package com.lightningkite.kwift.swift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser
import java.lang.IllegalStateException


fun SwiftAltListener.handleFunctionBodyAfterOpeningBrace(writer: TabWriter, item: KotlinParser.FunctionBodyContext) {
    with(writer) {
        tab {
            item.expression()?.let {
                line {
                    append("return ")
                    write(it)
                }
            }
            item.block()?.statements()?.let {
                it.statement().forEach {
                    startLine()
                    write(it)
                }
            }
        }
        line("}")
    }
}

fun SwiftAltListener.handleNormalFunction(
    tabWriter: TabWriter,
    item: KotlinParser.FunctionDeclarationContext,
    excludeBody: Boolean = false,
    usingTypeParameters: List<KotlinParser.TypeParameterContext>? = item.typeParameters()?.typeParameter()
) = with(tabWriter) {

    val myName = item.simpleIdentifier().text
    val owningClass = item.parentIfType<KotlinParser.DeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
        ?.parentIfType<KotlinParser.ClassBodyContext>()
        ?.parentIfType<KotlinParser.ClassDeclarationContext>()
    val isTopLevel = item.parentIfType<KotlinParser.DeclarationContext>()
        ?.parentIfType<KotlinParser.TopLevelObjectContext>() != null
    val originalUsesOverride =
        item.modifiers()?.modifier()?.any { it.memberModifier()?.OVERRIDE() != null } ?: false
    val needsOverrideKeyword = originalUsesOverride && owningClass?.implements()
        ?.any { myName in it.methods } != true

    fun Appendable.writeFunctionHeader(addUnderscore: Boolean) {
        if (needsOverrideKeyword) append("override ")
        if (owningClass != null && owningClass.INTERFACE() == null || isTopLevel) {
            append(item.modifiers().visibilityString())
            append(" ")
        }
        append("func ")
        append(item.simpleIdentifier().text)
        usingTypeParameters?.let {
            writeTypeArguments(tabWriter, it)
        }
        append("(")
        item.functionValueParameters().functionValueParameter().forEachBetween(
            forItem = {
                if (addUnderscore) {
                    append("_ ")
                }
                append(it.parameter().simpleIdentifier().text)
                append(": ")
                write(it.parameter().type())
                it.expression()?.let {
                    append(" = ")
                    write(it)
                }
            },
            between = { append(", ") }
        )
        append(") -> ")

        item.type()?.let {
            write(it)
        } ?: run {
            append("Void")
        }
    }

    line {
        writeFunctionHeader(false)
        if (!excludeBody && item.functionBody() != null) {
            append(" {")
        }
    }

    if (!excludeBody) {
        item.functionBody()?.let { handleFunctionBodyAfterOpeningBrace(this, it) }
    }

    val needsAlternateWriting = !item.functionValueParameters().functionValueParameter().let {
        it.size == 0 || (it.size == 1 && it.first()?.parameter()?.type()?.getUnderlyingType()?.functionType() != null)
    }
    if (needsAlternateWriting) {
        line {
            writeFunctionHeader(true)
            if (!excludeBody && item.functionBody() != null) {
                append(" {")
            }
        }
        if (!excludeBody) {
            tab {
                line {
                    append("return ${item.simpleIdentifier().text}(")
                    item.functionValueParameters().functionValueParameter().forEachBetween(
                        forItem = {
                            append(it.parameter().simpleIdentifier().text)
                            append(": ")
                            append(it.parameter().simpleIdentifier().text)
                        },
                        between = { append(", ") }
                    )
                    append(")")
                }
            }
            line("}")
        }
    }
}

fun SwiftAltListener.writeTypeArguments(writer: TabWriter, it: List<KotlinParser.TypeParameterContext>) = with(writer) {
    direct.append("<")
    it.forEachBetween(
        forItem = {
            direct.append(it.simpleIdentifier().text)
            it.type()?.let {
                direct.append(": ")
                write(it)
            }
        },
        between = { direct.append(", ") }
    )
    direct.append(">")
}

fun SwiftAltListener.registerFunction() {
    handle<KotlinParser.ParameterContext> { item ->
        direct.append(item.simpleIdentifier().text)
        direct.append(": ")
        write(item.type())
    }


    fun TabWriter.handleExtensionFunction(item: KotlinParser.FunctionDeclarationContext) {
        val typeArgumentNames =
            item.typeParameters()?.typeParameter()?.map { it.simpleIdentifier().text }?.toSet() ?: setOf()

        fun findUsages(item: ParserRuleContext): Sequence<String> {
            return when (item) {
                is KotlinParser.SimpleIdentifierContext -> sequenceOf(item.text)
                else -> item.children.asSequence().mapNotNull { it as? ParserRuleContext }.flatMap { findUsages(it).asSequence() }
            }
        }

        val typeArgumentsInReceiver =
            findUsages(item.receiverType()).distinct().filter { it in typeArgumentNames }.toSet()
        val otherTypeArguments = typeArgumentNames - typeArgumentsInReceiver
        val receiverWithoutParameters =
            item.receiverType().getUserType().simpleUserType().last().simpleIdentifier()!!.text

        line("extension ${receiverWithoutParameters?.let { typeReplacements[it] ?: it }} {")
        tab {
            handleNormalFunction(
                this,
                item,
                usingTypeParameters = item.typeParameters()?.typeParameter()?.filter { it.simpleIdentifier().text in otherTypeArguments }?.takeUnless { it.isEmpty() })
        }
        line("}")
    }

    handle<KotlinParser.FunctionDeclarationContext> { item ->
        if (item.receiverType() != null) this.handleExtensionFunction(item)
        else handleNormalFunction(this, item)
    }
    handle<KotlinParser.FunctionValueParametersContext> {
        direct.append("(")
        it.functionValueParameter().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append(")")
    }
    handle<KotlinParser.ValueArgumentsContext> {
        direct.append("(")
        it.valueArgument().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append(")")
    }
    handle<KotlinParser.ValueArgumentContext> {
        it.simpleIdentifier()?.let {
            direct.append(it.text + ": ")
        }
        write(it.expression())
    }
    handle<KotlinParser.TypeParametersContext> {
        writeTypeArguments(this, it.typeParameter())
    }
}

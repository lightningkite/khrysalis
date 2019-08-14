package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun KotlinParser.FunctionDeclarationContext.receiver(): KotlinParser.TypeContext? {
    return children.asSequence()
        .takeWhile { it !is KotlinParser.FunctionValueParametersContext }
        .mapNotNull { it as? KotlinParser.TypeContext }
        .firstOrNull()
}

fun KotlinParser.FunctionDeclarationContext.returnType(): KotlinParser.TypeContext? {
    return children.asSequence()
        .dropWhile { it !is KotlinParser.FunctionValueParametersContext }
        .mapNotNull { it as? KotlinParser.TypeContext }
        .firstOrNull()
}

fun SwiftAltListener.registerFunction() {
    handle<KotlinParser.ParameterContext> { item ->
        direct.append(item.simpleIdentifier().text)
        direct.append(": ")
        write(item.type())
    }
    fun TabWriter.handleFunctionBodyAfterOpeningBrace(item: KotlinParser.FunctionBodyContext) {
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

    fun TabWriter.handleNormalFunction(item: KotlinParser.FunctionDeclarationContext) {

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

        fun Appendable.writeFunctionHeader(addUnderscore: Boolean){
            if (needsOverrideKeyword) append("override ")
            if (owningClass != null || isTopLevel) {
                append(item.modifiers().visibilityString())
                append(" ")
            }
            append("func ")
            append(item.simpleIdentifier().text)
            item.typeParameters()?.let { write(it) }
            append("(")
            item.functionValueParameters().functionValueParameter().forEachBetween(
                forItem = {
                    if(addUnderscore){
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

            item.returnType()?.let {
                write(it)
            } ?: run {
                append("Void")
            }
        }

        line {
            writeFunctionHeader(false)
            if (item.functionBody() != null) {
                append(" {")
            }
        }

        item.functionBody()?.let { handleFunctionBodyAfterOpeningBrace(it) }

        val needsAlternateWriting = item.functionValueParameters().functionValueParameter().any {
            val t = it.parameter().type()
            t != null && t.functionType() == null
        }
        if (needsAlternateWriting) {
            line {
                writeFunctionHeader(true)
                if (item.functionBody() != null) {
                    append(" {")
                }
            }
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

    fun TabWriter.handleExtensionFunction(item: KotlinParser.FunctionDeclarationContext) {
        line("//Extension functions not supported yet.")
    }

    handle<KotlinParser.FunctionDeclarationContext> { item ->
        if (item.receiver() != null) this.handleExtensionFunction(item)
        else this.handleNormalFunction(item)
    }
    handle<KotlinParser.PostfixUnaryExpressionContext> { item ->
        if (item.postfixUnarySuffix()?.oneOnly()?.callSuffix() != null) {
            val repl = functionReplacements[item.primaryExpression().text]
            if (repl != null) {
                direct.append(repl)
            } else {
                write(item.primaryExpression())
            }
            item.postfixUnarySuffix().forEach { write(it) }
        } else {
            write(item.primaryExpression())
            item.postfixUnarySuffix().forEach { write(it) }
        }
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
        direct.append("<")
        it.typeParameter().forEachBetween(
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
}

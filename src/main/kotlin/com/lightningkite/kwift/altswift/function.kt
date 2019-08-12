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
    fun TabWriter.handleFunctionBodyAfterOpeningBrace(item: KotlinParser.FunctionBodyContext) {
        tab {
            item.expression()?.let {
                line {
                    append("return ")
                    write(it)
                }
            }
            item.block()?.let {
                it.statement().forEach {
                    startLine()
                    write(it)
                }
            }
        }
        line("}")
    }

    fun TabWriter.handleNormalFunction(item: KotlinParser.FunctionDeclarationContext) {

        val myName = item.identifier().text
        val owningClass = item.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
            ?.parentIfType<KotlinParser.ClassBodyContext>()
            ?.parentIfType<KotlinParser.ClassDeclarationContext>()
        val originalUsesOverride = item.modifierList()?.modifier()?.any{ it.memberModifier()?.OVERRIDE() != null } ?: false
        val needsOverrideKeyword = originalUsesOverride && owningClass?.implements()
            ?.any { myName in it.methods } != true

        line {
            if(needsOverrideKeyword) append("override ")
            if(owningClass != null){
                append(item.modifierList().visibilityString())
                append(" ")
            }
            append("func ")
            append(item.identifier().text)
            item.typeParameters()?.let {
                append(it.typeParameter().joinToString(", ", "<", ">") {
                    val typeConstraint = it.type()?.let { ": ${it.toSwift()}" } ?: ""
                    it.simpleIdentifier().text + typeConstraint
                })
            }
            append("(")
            item.functionValueParameters().functionValueParameter().forEachBetween(
                forItem = {
                    append(it.parameter().simpleIdentifier().text)
                    append(": ")
                    append(it.parameter().type().toSwift())
                    it.expression()?.let {
                        append(" = ")
                        write(it)
                    }
                },
                between = { append(", ") }
            )
            append(") -> ")
            append(item.returnType()?.toSwift() ?: "Unit")
            if(item.functionBody() != null) append(" {")
        }

        item.functionBody()?.let { handleFunctionBodyAfterOpeningBrace(it) }

        val needsAlternateWriting = item.functionValueParameters().functionValueParameter().any {
            val t = it.parameter().type()
            t != null && t.functionType() == null
        }
        if (needsAlternateWriting) {
            line {
                append(item.modifierList().visibilityString())
                if(needsOverrideKeyword) append(" override")
                append(" func ")
                append(item.identifier().text)
                item.typeParameters()?.let {
                    append(it.typeParameter().joinToString(", ", "<", ">") {
                        val typeConstraint = it.type()?.let { ": ${it.toSwift()}" } ?: ""
                        it.simpleIdentifier().text + typeConstraint
                    })
                }
                append("(")
                item.functionValueParameters().functionValueParameter().forEachBetween(
                    forItem = {
                        append("_ ")
                        append(it.parameter().simpleIdentifier().text)
                        append(": ")
                        append(it.parameter().type().toSwift())
                        it.expression()?.let {
                            append(" = ")
                            write(it)
                        }
                    },
                    between = { append(", ") }
                )
                append(") -> ")
                append(item.returnType()?.toSwift() ?: "Unit")
                if (item.functionBody() != null) {
                    append(" {")
                }
            }
            tab {
                line {
                    append("return ${item.identifier().text}(")
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
    handle<KotlinParser.CallExpressionContext> {
        val repl = functionReplacements[it.assignableExpression().text]
        if (repl != null) {
            direct.append(repl)
        } else {
            write(it.assignableExpression())
        }
        it.typeArguments()?.let {
            write(it)
        }
        it.valueArguments()?.let {
            write(it)
        }
        it.annotatedLambda()?.let {
            write(it)
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
}

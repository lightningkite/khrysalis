package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.actuals.visibility
import com.lightningkite.khrysalis.swift.handleLet
import com.lightningkite.khrysalis.swift.parentIfType
import com.lightningkite.khrysalis.swift.writeTypeArguments
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerFunction() {

    this.handle<KotlinParser.FunctionDeclarationContext> {

        fun emitStandardFunction(it: KotlinParser.FunctionDeclarationContext, beforeStart: Appendable.() -> Unit) {
            line {
                beforeStart()
                write(it.simpleIdentifier())
                it.typeParameters()?.let {
                    append('<')
                    it.typeParameter().forEachBetween(
                        forItem = {
                            write(it.simpleIdentifier())
                            it.type()?.let {
                                append(" extends ")
                                write(it)
                            }
                        },
                        between = { append(", ") }
                    )
                    append('>')
                }
                append("(")
                it.functionValueParameters().functionValueParameter().forEachBetween(
                    forItem = {
                        write(it.parameter().simpleIdentifier())
                        append(": ")
                        write(it.parameter().type())
                        it.expression()?.let {
                            append(" = ")
                            write(it)
                        }
                    },
                    between = { append(", ") }
                )
                append(")")
                it.type()?.let {
                    append(": ")
                    write(it)
                    append(" ")
                } ?: run {
                    append(": void ")
                }
                it.functionBody()?.let {
                    append("{")
                }
            }
            it.functionBody()?.let {
                tab {
                    it.expression()?.let {
                        line {
                            append("return ")
                            write(it)
                            append(";")
                        }
                    }
                    it.block()?.statements()?.let { write(it) }
                }
                line("}")
            }
        }

        //CASES: Top-level, method, extension, inside other function
        val isTopLevel = it
            .parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.TopLevelObjectContext>() != null
        val isMethod = it
            .parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>() != null
        val isExtension = it.receiverType() != null

        when {
            isExtension -> {
            }
            isTopLevel -> {
                emitStandardFunction(it) {
                    if (it.visibility().isExposed) {
                        append("export ")
                    }
                    append("function ")
                }
            }
            isMethod -> {
            }
            else -> {
                emitStandardFunction(it) {
                    append("function ")
                }
            }
        }

    }

    handle<KotlinParser.StatementsContext> {
        it.statement().forEach {
            line {
                write(it)
                append(';')
            }
        }
    }
}

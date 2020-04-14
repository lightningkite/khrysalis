package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser.*

fun TypescriptTranslator.registerFunction() {

    val normalFunctionHandler = handle<FunctionDeclarationContext> {
        val rule = typedRule
        line {
            -(rule.simpleIdentifier())
            rule.typeParameters()?.let {
                -('<')
                it.typeParameter().forEachBetween(
                    forItem = {
                        -(it.simpleIdentifier())
                        it.type()?.let {
                            -(" extends ")
                            -(it)
                        }
                    },
                    between = { -(", ") }
                )
                -('>')
            }
            -("(")
            rule.functionValueParameters().functionValueParameter().forEachBetween(
                forItem = {
                    -(it.parameter().simpleIdentifier())
                    -(": ")
                    -(it.parameter().type())
                    it.expression()?.let {
                        -(" = ")
                        -(it)
                    }
                },
                between = { -(", ") }
            )
            -(")")
            rule.type()?.let {
                -(": ")
                -(it)
                -(" ")
            } ?: run {
                -(": void ")
            }
            rule.functionBody()?.let {
                -("{")
            }
        }
        rule.functionBody()?.let {
            it.expression()?.let {
                line {
                    -("return ")
                    -(it)
                    -(";")
                }
            }
            it.block()?.statements()?.let { -(it) }
            line("}")
        }
    }

    this.handle<StatementsContext> {
        typedRule.statement().forEachBetween(
            forItem = { -it },
            between = { -";\n" }
        )
        -"\n"
    }

    this.handle<FunctionDeclarationContext>(
        condition = { rule.parentIfType<DeclarationContext>()?.parentIfType<TopLevelObjectContext>() != null },
        priority = 10,
        action = {
            val rule = typedRule
            -"export function "
            doSuper()
        }
    )
}

package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.web.typescript.actuals.visibility
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParser.*

fun TypescriptTranslator.registerFunction() {

    val normalFunctionHandler = handle<FunctionDeclarationContext> {
        val rule = typedRule
        line {
            -(rule.simpleIdentifier())
            -rule.typeParameters()
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
    this.handle<FunctionDeclarationContext>(
        condition = {typedRule.parentIfType<DeclarationContext>()
            ?.parentIfType<ClassMemberDeclarationContext>()
            ?.parentIfType<ClassMemberDeclarationsContext>()
            ?.parentIfType<ClassBodyContext>()
            ?.parentIfType<CompanionObjectContext>() != null },
        priority = 11,
        action = {
            typedRule.visibility().let {
                -it.name.toLowerCase()
                -" "
            }
            -"static "
            doSuper()
        }
    )
    this.handle<FunctionDeclarationContext>(
        condition = {typedRule.parentIfType<DeclarationContext>()
            ?.parentIfType<ClassMemberDeclarationContext>()
            ?.parentIfType<ClassMemberDeclarationsContext>()
            ?.parentIfType<ClassBodyContext>()
            ?.parentIfType<ClassDeclarationContext>() != null },
        priority = 11,
        action = {
            typedRule.visibility().let {
                -it.name.toLowerCase()
                -" "
            }
            doSuper()
        }
    )
    this.handle<FunctionDeclarationContext>(
        condition = {typedRule.parentIfType<DeclarationContext>()
            ?.parentIfType<ClassMemberDeclarationContext>()
            ?.parentIfType<ClassMemberDeclarationsContext>()
            ?.parentIfType<ClassBodyContext>()
            ?.parentIfType<ClassDeclarationContext>()
            ?.INTERFACE() != null
        },
        priority = 100,
        action = {
            val rule = typedRule
            -(rule.simpleIdentifier())
            -rule.typeParameters()
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
            -";\n"
        }
    )
}

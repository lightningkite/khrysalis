package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.addReturn
import com.lightningkite.khrysalis.generic.set
import com.lightningkite.khrysalis.ios.swift.TabWriter
import com.lightningkite.khrysalis.ios.swift.oneOnly
import com.lightningkite.khrysalis.ios.swift.parentOfType
import com.lightningkite.khrysalis.ios.swift.usedAsStatement
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParser.*
import java.lang.IllegalArgumentException

fun TypescriptTranslator.registerControl() {
    handle<ForStatementContext> {
        val rule = typedRule
        -"for (const "
        -rule.variableDeclaration()!!
        -" of "
        -rule.expression()
        -") "
        -rule.controlStructureBody()
    }

    handle<BlockContext> {
        val rule = typedRule
        -"{\n"
        rule.statements().statement().forEachBetween(
            forItem = { -it },
            between = { -";\n" }
        )
        -"\n}"
    }

    handle<WhileStatementContext> {
        val rule = typedRule
        -"while ("
        -rule.expression()
        -") "
        -rule.controlStructureBody()
    }

    handle<IfExpressionContext>(
        condition = { rule.usedAsStatement() },
        priority = 100
    ) {
        val rule = typedRule
        -"if ("
        -rule.expression()
        -")"
        -rule.controlStructureBody(0)
        rule.controlStructureBody(1)?.let {
            -" else "
            -it
        }
    }

    val normalWhen = handle<WhenExpressionContext>(
        condition = {
            val rule = typedRule
            rule.usedAsStatement() && rule.whenSubject() != null &&
                    rule.whenEntry()
                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
        },
        priority = 100
    ) {
        val rule = typedRule
        rule.whenSubject().variableDeclaration()?.let {
            -it
            -" = "
            -rule.whenSubject().expression()
            -";\n"
            -"switch("
            -it.simpleIdentifier()
            -") {\n"
        } ?: run {
            -"switch("
            -rule.whenSubject().expression()
            -") {\n"
        }
        for (entry in rule.whenEntry()) {
            entry.ELSE()?.let {
                -"default:\n"
                entry.controlStructureBody()?.let {
                    it.block()?.let {
                        it.statements()?.statement()?.forEach {
                            -it
                            -";\n"
                        }
                    } ?: it.statement()?.let {
                        -it
                        -";\n"
                    }
                }
                -"break;\n"
            } ?: entry.whenCondition().let {
                for (cond in it) {
                    -"case "
                    -cond.expression()
                    -":\n"
                }
                entry.controlStructureBody()?.let {
                    it.block()?.let {
                        it.statements()?.statement()?.forEach {
                            -it
                            -";\n"
                        }
                    } ?: it.statement()?.let {
                        -it
                        -";\n"
                    }
                }
                -"break;\n"
            }
        }
        -"}"
    }

    handle<WhenExpressionContext>(
        condition = {
            val rule = typedRule
            rule.whenSubject() != null &&
                    rule.whenEntry()
                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
        },
        priority = 50
    ) {
        val rule = typedRule
        -"() => {\n"
        rule.whenSubject().variableDeclaration()?.let {
            -it
            -" = "
            -rule.whenSubject().expression()
            -";\n"
            -"switch("
            -it.simpleIdentifier()
            -") {\n"
        } ?: run {
            -"switch("
            -rule.whenSubject().expression()
            -") {\n"
        }
        for (entry in rule.whenEntry()) {
            entry.ELSE()?.let {
                -"default:\n"
                entry.controlStructureBody()?.let {
                    it.block()?.let {
                        it.statements()?.statement()?.dropLast(1)?.forEach {
                            -it
                            -";\n"
                        }
                        it.statements()?.statement()?.lastOrNull()?.let {
                            -"return "
                            -it
                            -";\n"
                        }
                    } ?: it.statement()?.let {
                        -"return "
                        -it
                        -";\n"
                    }
                }
                -"break;\n"
            } ?: entry.whenCondition().let {
                for (cond in it) {
                    -"case "
                    -cond.expression()
                    -":\n"
                }
                entry.controlStructureBody()?.let {
                    it.block()?.let {
                        it.statements()?.statement()?.dropLast(1)?.forEach {
                            -it
                            -";\n"
                        }
                        it.statements()?.statement()?.lastOrNull()?.let {
                            -"return "
                            -it
                            -";\n"
                        }
                    } ?: it.statement()?.let {
                        -"return "
                        -it
                        -";\n"
                    }
                }
                -"break;\n"
            }
        }
        -"}"
        -"\n}"
    }
}

package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun <E> List<E>.oneOnly(): E? = if (this.size == 1) first() else null
fun KotlinParser.PrimaryExpressionContext.usedAsStatement() =
    parentOfType<KotlinParser.AssignableExpressionContext>()
        ?.parentOfType<KotlinParser.PostfixUnaryExpressionContext>()
        ?.parentOfType<KotlinParser.PrefixUnaryExpressionContext>()
        ?.parentOfType<KotlinParser.AsExpressionContext>()
        ?.parentOfType<KotlinParser.MultiplicativeExpressionContext>()
        ?.parentOfType<KotlinParser.AdditiveExpressionContext>()
        ?.parentOfType<KotlinParser.RangeExpressionContext>()
        ?.parentOfType<KotlinParser.InfixFunctionCallContext>()
        ?.parentOfType<KotlinParser.ElvisExpressionContext>()
        ?.parentOfType<KotlinParser.InfixOperationContext>()
        ?.parentOfType<KotlinParser.ComparisonContext>()
        ?.parentOfType<KotlinParser.EqualityContext>()
        ?.parentOfType<KotlinParser.ConjunctionContext>()
        ?.parentOfType<KotlinParser.DisjunctionContext>()
        ?.parentOfType<KotlinParser.AssignmentContext>()
        ?.parentOfType<KotlinParser.ExpressionContext>()
        ?.parentOfType<KotlinParser.StatementContext>() != null

fun SwiftAltListener.registerControl() {
    fun TabWriter.writeInsideIf(expression: KotlinParser.EqualityContext) {
        if (expression.equalityOperator()?.oneOnly()?.EXCL_EQ() != null
            && expression.comparison(0)?.text?.all { it.isLetterOrDigit() } == true
            && expression.comparison(1)?.text == "null"
        ) {
            direct.append("let ")
            direct.append(expression.comparison(0)!!.text!!)
            direct.append(" = ")
            direct.append(expression.comparison(0)!!.text!!)
        } else {
            val isExpr = expression.comparison()?.oneOnly()
                ?.infixOperation()?.oneOnly()
            if (isExpr?.isOperator?.oneOnly()?.IS() != null
                && expression.comparison(0)?.text?.all { it.isLetterOrDigit() } == true
            ) {
                direct.append("let ")
                direct.append(isExpr.elvisExpression(0)!!.text!!)
                direct.append(" = ")
                direct.append(isExpr.elvisExpression(0)!!.text!!)
                direct.append(" as ")
                direct.append(isExpr.elvisExpression(1)!!.text!!)
            } else {
                write(expression)
            }
        }
    }
    handle<KotlinParser.IfExpressionContext> { item ->
        var current = item
        while (true) {
            direct.append("if ")
            current.expression()?.disjunction()?.conjunction()?.oneOnly()?.let {
                it.equality().forEachBetween(
                    forItem = {
                        writeInsideIf(it)
                    },
                    between = { direct.append(", ") }
                )
            } ?: run {
                write(current.expression())
            }
            direct.append(" {")
            tab {
                current.controlStructureBody(0)?.let {
                    write(it)
                }
            }
            startLine()
            direct.append("}")

            if (current.ELSE() != null) {
                direct.append(" else ")
                val nextIf = current.controlStructureBody(1)
                    ?.statement()
                    ?.expression()
                    ?.disjunction()
                    ?.conjunction()?.oneOnly()
                    ?.equality()?.oneOnly()
                    ?.comparison()?.oneOnly()
                    ?.infixOperation()?.oneOnly()
                    ?.elvisExpression()?.oneOnly()
                    ?.infixFunctionCall()?.oneOnly()
                    ?.rangeExpression()?.oneOnly()
                    ?.additiveExpression()?.oneOnly()
                    ?.multiplicativeExpression()?.oneOnly()
                    ?.asExpression()?.oneOnly()
                    ?.prefixUnaryExpression()
                    ?.postfixUnaryExpression()
                    ?.primaryExpression()
                    ?.ifExpression()
                if (nextIf != null) {
                    current = nextIf
                } else {
                    direct.append("{")
                    tab {
                        current.controlStructureBody(1)?.let {
                            write(it)
                        }
                    }
                    startLine()
                    direct.append("}")
                    break
                }
            } else {
                break
            }
        }
    }
    handle<KotlinParser.WhenExpressionContext> { item ->
        val usedAsStatement = item
            .parentOfType<KotlinParser.PrimaryExpressionContext>()
            ?.usedAsStatement() ?: false

        item.whenSubject()?.expression()?.let { on ->
            direct.append("switch ")
            write(on)
            direct.append(" {")
            item.whenEntry().forEach {
                if (it.ELSE() != null) {
                    line("default:")
                    tab {
                        startLine()
                        write(it.controlStructureBody())
                    }
                } else {
                    val conditions = it.whenCondition()!!
                    if (conditions.size == 1) {
                        val c = conditions.first()
                        c.expression()?.let { expr ->
                            startLine()
                            direct.append("case ")
                            write(expr)
                            direct.append(":")
                            tab {
                                startLine()
                                write(it.controlStructureBody())
                            }
                        }
                        c.rangeTest()?.let { r ->
                            startLine()
                            direct.append("case ")
                            write(r.expression())
                            direct.append(":")
                            tab {
                                startLine()
                                write(it.controlStructureBody())
                            }
                        }
                        c.typeTest()?.let { t ->
                            startLine()
                            direct.append("case let ${on.text} as ")
                            write(t.type())
                            direct.append(":")
                            tab {
                                startLine()
                                write(it.controlStructureBody())
                            }
                        }
                    } else {
                        conditions.forEachBetween(
                            forItem = { c ->
                                c.expression()?.let { expr ->
                                    startLine()
                                    direct.append("case ")
                                    write(expr)
                                }
                                c.rangeTest()?.let { r ->
                                    startLine()
                                    direct.append("case ")
                                    write(r.expression())
                                }
                                c.typeTest()?.let {
                                    startLine()
                                    direct.append("case is ")
                                    write(it.type())
                                }
                            },
                            between = {
                                direct.append(", ")
                            }
                        )
                        direct.append(":")
                        tab {
                            startLine()
                            write(it.controlStructureBody())
                        }
                    }
                }
            }
            startLine()
            direct.append("}")
        } ?: run {
            item.whenEntry().forEachBetween(
                forItem = {
                    direct.append("if ")
                    it.whenCondition().forEachBetween(
                        forItem = { cond ->
                            write(cond.expression()!!)
                        },
                        between = { direct.append(" || ") }
                    )
                    direct.append(" {")
                    tab {
                        startLine()
                        write(it.controlStructureBody())
                    }
                    startLine()
                    direct.append("}")
                },
                between = {
                    direct.append(" else ")
                }
            )
        }

    }
    handle<KotlinParser.ControlStructureBodyContext> { item ->
        item.block()?.statements()?.statement()?.forEach {
            startLine()
            write(it)
        }
        item.statement()?.let {
            startLine()
            write(it)
        }
    }
}

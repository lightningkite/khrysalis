package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.swift.TabWriter
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.forEachBetweenIndexed
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.handleLet(
    writer: TabWriter,
    item: KotlinParser.PostfixUnaryExpressionContext,
    secondToLastSuffix: KotlinParser.PostfixUnarySuffixContext
) = with(writer) {
    val lambda =
        item.postfixUnarySuffix()?.last()?.callSuffix()?.annotatedLambda()?.lambdaLiteral()!!

    fun writeOn() {
        item.children?.takeWhile { it != secondToLastSuffix }?.forEachBetween(
            forItem = { child ->
                when (child) {
                    is ParserRuleContext -> write(child)
                    is TerminalNode -> write(child)
                }
            },
            between = {}
        )
    }
    direct.append("{ () in ")
    tab {
        line {
            append("if let ")
            append(
                lambda.lambdaParameters()?.lambdaParameter(0)?.variableDeclaration()?.simpleIdentifier()?.text
                    ?: "it"
            )
            append(" = (")
            writeOn()
            append(") {")
        }
        tab {
            lambda.statements().statement().dropLast(1).forEach {
                startLine()
                write(it)
            }
            lambda.statements().statement().last().let {
                startLine()
                direct.append("return ")
                write(it.expression()!!)
            }
        }
        line("}")
        line("return nil")
    }
    startLine()
    direct.append("}()")
}


data class IfLets(val parts: List<Pair<NullableExpression?, ParserRuleContext>>)
data class NullableExpression(val parts: List<ParseTree>, val name: String)

fun KotlinParser.StatementContext.letIfElses(): IfLets? {
    val elvisExp = this
        .expression()
        ?.disjunction()
        ?.conjunction()?.oneOnly()
        ?.equality()?.oneOnly()
        ?.comparison()?.oneOnly()
        ?.infixOperation()?.oneOnly()
        ?.elvisExpression()?.oneOnly()
        ?: return null

    //First expression must have a `?.let` or we return null
    if(elvisExp.infixFunctionCall(0)
        ?.rangeExpression()?.oneOnly()
        ?.additiveExpression()?.oneOnly()
        ?.multiplicativeExpression()?.oneOnly()
        ?.asExpression()?.oneOnly()
        ?.prefixUnaryExpression()
        ?.postfixUnaryExpression()
        ?.postfixUnarySuffix()
        ?.let { it.getOrNull(it.lastIndex - 1) }
        ?.let { secondToLastSuffix ->
            secondToLastSuffix.navigationSuffix()?.memberAccessOperator()?.safeNav() != null &&
                    secondToLastSuffix.navigationSuffix()?.simpleIdentifier()?.text == "let"
        } != true){
        return null
    }

    val results = elvisExp.infixFunctionCall().map {
        val postfix = it.rangeExpression().oneOnly()
            ?.additiveExpression()?.oneOnly()
            ?.multiplicativeExpression()?.oneOnly()
            ?.asExpression()?.oneOnly()
            ?.prefixUnaryExpression()
            ?.postfixUnaryExpression()
        if (postfix != null) {
            val secondToLastSuffix = postfix.postfixUnarySuffix().let { it.getOrNull(it.lastIndex - 1) }
            if (secondToLastSuffix != null &&
                secondToLastSuffix.navigationSuffix()?.memberAccessOperator()?.safeNav() != null &&
                secondToLastSuffix.navigationSuffix()?.simpleIdentifier()?.text == "let"
            ) {
                val lambda = postfix.postfixUnarySuffix()?.last()?.callSuffix()?.annotatedLambda()?.lambdaLiteral()!!
                NullableExpression(
                    parts = postfix.children!!.takeWhile { it != secondToLastSuffix }.toList(),
                    name = lambda.lambdaParameters()?.lambdaParameter(0)?.variableDeclaration()?.simpleIdentifier()?.text
                        ?: "it"
                ) to lambda.statements()
            } else if(postfix.primaryExpression()?.text == "run" &&
                        postfix.postfixUnarySuffix()?.size == 1
            ){
                (null to (postfix.postfixUnarySuffix(0)?.callSuffix()?.annotatedLambda()?.lambdaLiteral()?.statements() ?: it))
            } else {
                (null to it)
            }
        } else (null to it)
    }
    return when (results.size) {
        0 -> null
        1 -> if (results.first().first != null) IfLets(results) else null
        else -> IfLets(results)
    }
}

fun SwiftAltListener.handleLet(
    writer: TabWriter,
    ifLets: IfLets
) = with(writer) {
    ifLets.parts.forEachBetweenIndexed(
        forItem = { index, item ->
            item.first?.let { cond ->
                line {
                    append("if let ")
                    append(cond.name)
                    append(" = (")
                    cond.parts.forEachBetween(
                        forItem = { child ->
                            when (child) {
                                is ParserRuleContext -> write(child)
                                is TerminalNode -> write(child)
                            }
                        },
                        between = {}
                    )
                    append(") {")
                }
                tab {
                    startLine()
                    write(item.second)
                }
                startLine()
                direct.append("}")
            } ?: if(index == ifLets.parts.lastIndex) {
                direct.append("{")
                tab {
                    startLine()
                    write(item.second)
                }
                startLine()
                direct.append("}")
            } else {
                direct.append("if let _ = ")
                write(item.second)
                direct.append("{}")
            }
        },
        between = { direct.append(" else ") }
    )
    ensureNewLine()
}

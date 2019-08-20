package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser


fun SwiftAltListener.handleLet(
    writer: TabWriter,
    item: KotlinParser.PostfixUnaryExpressionContext,
    secondToLastSuffix: KotlinParser.PostfixUnarySuffixContext
) = with(writer){
    val contextIsStatement = item.usedAsStatement()
    val lambda =
        item.postfixUnarySuffix()?.last()?.callSuffix()?.annotatedLambda()?.lambdaLiteral()!!
    fun writeOn(){
        item.children?.takeWhile { it != secondToLastSuffix }?.forEachBetween(
            forItem = { child ->
                when(child){
                    is ParserRuleContext -> write(child)
                    is TerminalNode -> write(child)
                }
            },
            between = {
                direct.append(" ")
            }
        )
    }
    if (contextIsStatement) {
        line {
            append("if let ")
            append(lambda.lambdaParameters().lambdaParameter(0).variableDeclaration().simpleIdentifier().text)
            append(" = ")
            writeOn()
            append(" {")
        }
        tab {
            lambda.statements().statement().forEach {
                startLine()
                write(it)
            }
        }
        line("}")
    } else {
        direct.append("{ () in ")
        tab {
            line {
                append("if let ")
                append(lambda.lambdaParameters().lambdaParameter(0).variableDeclaration().simpleIdentifier().text)
                append(" = ")
                writeOn()
                append(" {")
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
}

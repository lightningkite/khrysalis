package com.lightningkite.kwift.swift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerLambda() {
    handle<KotlinParser.LambdaLiteralContext> { literal ->
        generateSequence<RuleContext>(literal) { it.parent }.take(8).let {
            println("Rule Stack: ${it.joinToString { it::class.java.simpleName }}")
        }
        val annotations = literal
            .parentIfType<KotlinParser.FunctionLiteralContext>()
            ?.parentIfType<KotlinParser.PrimaryExpressionContext>()
            ?.parentIfType<KotlinParser.PostfixUnaryExpressionContext>()
            ?.parentIfType<KotlinParser.PrefixUnaryExpressionContext>()
            ?.also { println("X: " + it.text) }
            ?.unaryPrefix()
            ?.mapNotNull { it.annotation()?.singleAnnotation()?.unescapedAnnotation()?.text }
            ?: literal
                .parentIfType<KotlinParser.AnnotatedLambdaContext>()
                ?.also { println("X: " + it.text) }
                ?.annotation()?.mapNotNull { it.singleAnnotation()?.unescapedAnnotation()?.text }
            ?: listOf()

        direct.append("{ ")
        println("Lambda annotations: $annotations")
        if (annotations.any {it.startsWith("weakSelf") }) {
            direct.append("[weak self] ")
        }
        if (annotations.any {it.startsWith("unownedSelf") }) {
            direct.append("[unowned self] ")
        }
        direct.append("(")
        literal.lambdaParameters()?.lambdaParameter()?.forEachBetween(
            forItem = {
                direct.append(it.text)
            },
            between = {
                direct.append(", ")
            }
        )
        direct.append(") in ")
        tab {
            literal.statements().statement().forEach {
                startLine()
                write(it)
            }
        }
        startLine()
        direct.append("}")
    }
}

package com.lightningkite.kwift.swift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerLambda() {
    handle<KotlinParser.LambdaLiteralContext> { literal ->
        val annotations = literal
            .parentIfType<KotlinParser.FunctionLiteralContext>()
            ?.parentIfType<KotlinParser.PrimaryExpressionContext>()
            ?.parentIfType<KotlinParser.PostfixUnaryExpressionContext>()
            ?.parentIfType<KotlinParser.PrefixUnaryExpressionContext>()
            ?.unaryPrefix()
            ?.mapNotNull { it.annotation()?.singleAnnotation()?.unescapedAnnotation() }
            ?: literal
                .parentIfType<KotlinParser.AnnotatedLambdaContext>()
                ?.annotation()?.mapNotNull { it.singleAnnotation()?.unescapedAnnotation() }
            ?: listOf()

        direct.append("{ ")
        if (annotations.any {it.text.startsWith("weakSelf") }) {
            direct.append("[weak self] ")
        }
        if (annotations.any {it.text.startsWith("unownedSelf") }) {
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
        direct.append(") ")
        annotations.find { it.text.startsWith("swiftReturnType") }?.let {
            direct.append("-> ")
            direct.append(it.constructorInvocation()?.valueArguments()?.valueArgument(0)?.expression()?.text?.trim('"') ?: "???")
            direct.append(" ")
        }
        direct.append("in ")
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

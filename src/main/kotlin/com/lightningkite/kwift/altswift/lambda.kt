package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerLambda() {
    handle<KotlinParser.LambdaLiteralContext> { item ->
        direct.append("{ (")
        item.lambdaParameters()?.lambdaParameter()?.forEachBetween(
            forItem = {
                direct.append(it.text)
            },
            between = {
                direct.append(", ")
            }
        )
        direct.append(") in ")
        tab {
            item.statements().statement().forEach {
                startLine()
                write(it)
            }
        }
        line("}")
    }
}

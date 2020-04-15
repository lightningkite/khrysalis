package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptTranslator.registerLambda() {
    handle<KotlinParser.LambdaLiteralContext> {
        -"("
        -typedRule.lambdaParameters()
        -") => {"
        -typedRule.statements()
        -"}"
    }

    this.handle<KotlinParser.AnonymousFunctionContext> {
        -typedRule.parametersWithOptionalType()
        typedRule.type()?.lastOrNull()?.let {
            -": "
            -it
        }
        -" => "
        -typedRule.functionBody()?.block()
        -typedRule.functionBody()?.expression()
        -"\n"
    }
}

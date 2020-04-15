package com.lightningkite.khrysalis.web.typescript.actuals

import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.web.typescript.TypescriptTranslator
import org.jetbrains.kotlin.KotlinParser


fun TypescriptTranslator.registerLambdas() {
    this.handle<KotlinParser.LambdaLiteralContext> {
        -"("
        typedRule.lambdaParameters()?.let {
            -it.lambdaParameter().forEachBetween(
                forItem = {
                    -it.variableDeclaration()
                },
                between = {
                    -", "
                }
            )
        }
        -") => {\n"
        -typedRule.statements()
        -"}\n"
    }
    this.handle<KotlinParser.AnonymousFunctionContext> {
        -"("
        typedRule.parametersWithOptionalType()?.let {
            it.parameterWithOptionalType()?.forEachBetween(
                forItem = {
                    -it.simpleIdentifier()
                    it.type()?.let {
                        -": "
                        -it
                    }
                },
                between = {
                    -" , "
                }
            )
        }
        -") "
        typedRule.type()?.let {
            -": "
            -it
        }
        -" => {\n"
        -typedRule.functionBody()
        -"}\n"
    }
}
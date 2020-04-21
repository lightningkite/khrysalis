package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

fun TypescriptTranslator.registerLiterals() {
    handle<KtStringTemplateExpression> {
        if(typedRule.entries.all { it is KtLiteralStringTemplateEntry }){
            -typedRule.text
        } else {
            -'`'
            -typedRule.entries
            -'`'
        }
    }
    handle<KtSimpleNameStringTemplateEntry> {
        -"\${"
        -typedRule.expression
        -"}"
    }
    handle<KtBlockStringTemplateEntry> {
        -"\${"
        -typedRule.expression
        -"}"
    }

    handle<KtConstantExpression> {
        when(typedRule.node.elementType){
            KtTokens.INTEGER_LITERAL -> -typedRule.text.replace("_", "").removeSuffix("L").removeSuffix("l")
            KtTokens.CHARACTER_LITERAL -> -typedRule.text
            KtTokens.FLOAT_LITERAL -> -typedRule.text.removeSuffix("F").removeSuffix("f")
            else -> doSuper()
        }
    }
}

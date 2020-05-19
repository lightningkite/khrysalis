package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

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
            KtStubElementTypes.INTEGER_CONSTANT -> -typedRule.text.replace("_", "").removeSuffix("L").removeSuffix("l")
            KtStubElementTypes.CHARACTER_CONSTANT -> -typedRule.text
            KtStubElementTypes.FLOAT_CONSTANT -> -typedRule.text.removeSuffix("F").removeSuffix("f")
            else -> doSuper()
        }
    }
}

package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

fun TypescriptTranslator.registerLiterals() {
    handle<KtStringTemplateExpression>(
        condition = { typedRule.firstChild.text == "\"\"\"" },
        priority = 1,
        action = {
            -'"'
            -typedRule.entries
            -'"'
        }
    )
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
    handle<KtLiteralStringTemplateEntry> {
        -typedRule.text.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")
    }

    """as\ndf sadf asd fa
        asdfadsf
    """.trimMargin()

    handle<KtConstantExpression> {
        when(typedRule.node.elementType){
            KtStubElementTypes.INTEGER_CONSTANT -> -typedRule.text.replace("_", "").removeSuffix("L").removeSuffix("l")
            KtStubElementTypes.CHARACTER_CONSTANT -> -typedRule.text
            KtStubElementTypes.FLOAT_CONSTANT -> -typedRule.text.removeSuffix("F").removeSuffix("f")
            else -> doSuper()
        }
    }
}

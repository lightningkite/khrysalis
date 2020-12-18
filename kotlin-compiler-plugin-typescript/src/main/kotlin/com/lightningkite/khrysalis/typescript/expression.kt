package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression

val comparisonTokens = setOf(KtTokens.LT, KtTokens.GT, KtTokens.LTEQ, KtTokens.GTEQ, KtTokens.EXCLEQ, KtTokens.EQEQ, KtTokens.EQEQEQ, KtTokens.EXCLEQEQEQ)
fun TypescriptTranslator.registerExpression() {
    handle<KtBinaryExpression>(
        condition = { typedRule.operationToken == KtTokens.ELVIS },
        priority = 1000,
        action = {
            val useParens = (typedRule.parent as? KtBinaryExpression)?.let { it.operationToken in comparisonTokens } == true
            if(useParens) -'('
            -typedRule.left
            -" ?? "
            -typedRule.right
            if(useParens) -')'
        }
    )
}

package com.lightningkite.khrysalis.analysis

import org.jetbrains.kotlin.lexer.KtTokens

object KotlinInfo {
    val neverExpressionTokens = setOf(
        KtTokens.EQ,
        KtTokens.PLUSEQ,
        KtTokens.MINUSEQ,
        KtTokens.MULTEQ,
        KtTokens.DIVEQ,
        KtTokens.PERCEQ
    )
    val dontReturnTypes = setOf("kotlin.Unit", "kotlin.Nothing")
}

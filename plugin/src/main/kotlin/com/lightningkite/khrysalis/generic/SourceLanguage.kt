package com.lightningkite.khrysalis.generic

import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser

class SourceLanguage(
    val tokenCount: Int
) {
    companion object {
        val kotlin = SourceLanguage(KotlinLexer.VOCABULARY.maxTokenType + 1)
    }
}

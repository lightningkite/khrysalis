package com.lightningkite.khrysalis.web.typescript.actuals

import com.lightningkite.khrysalis.flow.CodeSection
import com.lightningkite.khrysalis.flow.section
import com.lightningkite.khrysalis.ios.swift.TabWriter
import com.lightningkite.khrysalis.web.typescript.TypescriptTranslator
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

fun File.typescriptStubs(swift: TypescriptTranslator, to: File) {
}

internal enum class Visibility(val isExposed: Boolean) {
    Private(false),
    Internal(false),
    Protected(true),
    Public(true)
}

internal fun KotlinParser.ModifiersContext.visibility2(): Visibility {
    val v = this.modifier()?.asSequence()?.mapNotNull { it.visibilityModifier() }?.firstOrNull()
    return when {
        v == null -> Visibility.Public
        v.INTERNAL() != null -> Visibility.Internal
        v.PRIVATE() != null -> Visibility.Private
        v.PROTECTED() != null -> Visibility.Protected
        v.PUBLIC() != null -> Visibility.Public
        else -> Visibility.Public
    }
}

internal fun RuleContext.visibility(): Visibility {
    if (this is KotlinParser.ModifiersContext) return this.visibility2()
    if (this is ParserRuleContext) this.getRuleContext(
        KotlinParser.ModifiersContext::class.java,
        0
    )?.let { return it.visibility2() }
    this.parent?.let { return it.visibility() }
    return Visibility.Public
}



package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerStatement() {
    handle<KotlinParser.StatementContext> { item ->
        item.letIfElses()?.let { handleLet(this, it) } ?: defaultWrite(item)
    }
}

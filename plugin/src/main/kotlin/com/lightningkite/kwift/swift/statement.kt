package com.lightningkite.kwift.swift

import com.lightningkite.kwift.utils.camelCase
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerStatement() {
    handle<KotlinParser.StatementContext> { item ->
        item.letIfElses()?.let { handleLet(this, it) } ?: defaultWrite(item)
    }
}

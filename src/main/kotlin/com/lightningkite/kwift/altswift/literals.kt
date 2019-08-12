package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerLiterals() {
    handle<KotlinParser.LineStringLiteralContext> { item ->
        direct.append('"')
        item.children.forEach {
            when (it) {
                is KotlinParser.LineStringContentContext -> direct.append(it.text)
                is KotlinParser.LineStringExpressionContext -> {
                    direct.append("\\(")
                    write(it)
                    direct.append (")")
                }
            }
        }
        direct.append('"')
    }
}

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
                    write(it.expression())
                    direct.append (")")
                }
            }
        }
        direct.append('"')
    }
    handle<KotlinParser.ElvisContext> { item ->
        direct.append("??")
    }
    handle<KotlinParser.PostfixUnaryOperatorContext> { item ->
        if(item.excl() != null) direct.append("!")
        else defaultWrite(item)
    }
    tokenOptions[KotlinParser.RealLiteral] = {
        direct.append(it.text.removeSuffix("f").removeSuffix("F"))
    }
    tokenOptions[KotlinParser.LongLiteral] = {
        direct.append(it.text.removeSuffix("l").removeSuffix("L"))
    }
}

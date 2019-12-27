package com.lightningkite.kwift.swift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerLiterals() {
    handle<KotlinParser.LineStringLiteralContext> { item ->
        direct.append('"')
        item.children.forEach {
            when (it) {
                is KotlinParser.LineStringContentContext -> {
                    if(it.text.startsWith("$") && it.text.length > 1){
                        direct.append("\\(")
                        direct.append(it.text.removePrefix("$"))
                        direct.append(")")
                    } else {
                        direct.append(it.text.replace("\\$", "$"))
                    }
                }
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
        item.excl()?.let { direct.append("!") } ?:
            item.INCR()?.let { direct.append(" += 1")} ?:
            item.DECR()?.let { direct.append(" -= 1")}
    }
    tokenOptions[KotlinParser.RealLiteral] = {
        if(it.text.endsWith('f', true)){
            direct.append("Float(")
            direct.append(it.text.removeSuffix("f").removeSuffix("F"))
            direct.append(")")
        } else {
            direct.append(it.text)
        }
    }
    tokenOptions[KotlinParser.LongLiteral] = {
        direct.append(it.text.removeSuffix("l").removeSuffix("L"))
    }
    tokenOptions[KotlinParser.CharacterLiteral] = {
        direct.append(it.text.trim('\'').let { "\"$it\"" })
    }
}

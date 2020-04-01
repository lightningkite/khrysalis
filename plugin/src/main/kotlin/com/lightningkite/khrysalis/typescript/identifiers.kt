package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerIdentifiers(){
    handle<KotlinParser.SimpleIdentifierContext> {
        direct.append(when(val text = it.text){
            "string", "number", "boolean", "any" -> "a" + text.capitalize()
            else -> text
        })
    }
}

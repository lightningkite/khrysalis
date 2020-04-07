package com.lightningkite.khrysalis.web.typescript

import org.jetbrains.kotlin.KotlinParser

fun TypescriptTranslator.registerIdentifiers(){
    handle<KotlinParser.SimpleIdentifierContext> {
        -when(val text = rule.text){
            "string", "number", "boolean", "any", "void" -> "a" + text.capitalize()
            else -> text
        }
    }
}

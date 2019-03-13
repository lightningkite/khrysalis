package com.lightningkite.kwift.swift

val startKotlin = Regex("/// *Kotlin *Only", RegexOption.IGNORE_CASE)
val endKotlin = Regex("/// *End *Kotlin *Only", RegexOption.IGNORE_CASE)

fun String.ignoreKotlinOnly(): String {
    var ignore = false
    return lineSequence().map {
        if(it.contains(startKotlin)){
            ignore = true
            it
        } else if(it.contains(endKotlin)) {
            ignore = false
            it
        } else {
            if(ignore){
                "//" + it
            } else {
                it
            }
        }
    }.joinToString("\n")
}

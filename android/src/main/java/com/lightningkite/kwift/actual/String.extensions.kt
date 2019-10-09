package com.lightningkite.kwift.actual

fun String.humanify(): String {
    if(this.isEmpty()) return ""
    return this[0].toUpperCase() + this.replace(".", " - ").replace(Regex("[A-Z]")){ result ->
        " " + result.value
    }.replace('_', ' ').trim()
}

fun String.formatList(arguments: List<Any?>) = this.format(*arguments.toTypedArray())

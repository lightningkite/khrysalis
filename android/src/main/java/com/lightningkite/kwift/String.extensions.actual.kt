package com.lightningkite.kwift

fun String.humanify(): String {
    if(this.isEmpty()) return ""
    return this[0].toUpperCase() + this.replace(".", " - ").replace(Regex("[A-Z]")){ result ->
        " " + result.value
    }.replace('_', ' ').trim()
}

fun String.toSnakeCase(): String {
    val builder = StringBuilder(this.length * 3 / 2)
    for (char in this) {
        if (char.isUpperCase()) {
            builder.append('_')
            builder.append(char.toLowerCase())
        } else {
            builder.append(char)
        }
    }
    return builder.toString().trim('_')
}

fun String.formatList(arguments: List<Any?>) = this.format(*arguments.toTypedArray())

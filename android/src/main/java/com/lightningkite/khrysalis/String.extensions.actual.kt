package com.lightningkite.khrysalis

/**
 *
 * Modifies the string to make it a more readable. This is capitalize the first letter, remove . and underscores through the string.
 *
 */
fun String.humanify(): String {
    if(this.isEmpty()) return ""
    return this[0].toUpperCase() + this.replace(".", " - ").replace(Regex("[A-Z]")){ result ->
        " " + result.value
    }.replace('_', ' ').trim()
}

/**
 *
 * Modifies the string to make follow the snake case standard. Adds underscore before an uppercase char, and then makes them all lowercase.
 *
 */
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

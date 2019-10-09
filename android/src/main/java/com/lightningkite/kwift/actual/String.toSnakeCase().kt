package com.lightningkite.kwift.actual

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

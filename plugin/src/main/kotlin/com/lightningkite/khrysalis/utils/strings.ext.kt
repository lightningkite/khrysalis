package com.lightningkite.khrysalis.utils


fun String.snakeCase(): String = this.replace(Regex("[A-Z]+")) { "_" + it.value.toLowerCase() }.trim('_')
fun String.javaify(): String = this
        .replace(Regex("\\.([a-zA-Z])")) {
            it.groupValues[1].toUpperCase()
        }
        .replace(".", "")
        .javaifyWithDots()

fun String.javaifyWithDots(): String = this
        .replace(Regex(",([a-zA-Z])")) {
            it.groupValues[1].toUpperCase()
        }
        .replace(",", "")

        .replace(Regex("-([a-zA-Z])")) {
            it.groupValues[1].toUpperCase()
        }
        .replace("-", "")

        .replace(Regex("_([a-zA-Z])")) {
            it.groupValues[1].toUpperCase()
        }
        .replace("_", "")

        .replace(Regex(" ([a-zA-Z])")) {
            it.groupValues[1].toUpperCase()
        }
        .replace(" ", "")

fun String.camelCase(): String {
    var nextIsUppercase = false
    return buildString(length) {
        for (c in this@camelCase) {
            if (c == '_') {
                nextIsUppercase = true
            } else {
                if (nextIsUppercase) {
                    append(c.toUpperCase())
                    nextIsUppercase = false
                } else {
                    append(c)
                }
            }
        }
    }
}

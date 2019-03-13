package com.lightningkite.kwift.utils


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

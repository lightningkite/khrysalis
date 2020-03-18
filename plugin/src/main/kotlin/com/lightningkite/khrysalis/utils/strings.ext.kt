package com.lightningkite.khrysalis.utils

import java.io.File
import java.io.InputStream
import java.security.MessageDigest


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

fun InputStream.checksum(): String {
    val digest = MessageDigest.getInstance("MD5")
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    while (true) {
        val readBytes = this.read(buffer)
        if (readBytes == -1) break
        digest.update(buffer, 0, readBytes)
    }
    return digest.digest().joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
}

fun String.checksum(): String = this.byteInputStream().use { it.checksum() }
fun File.checksum(): String = this.inputStream().use { it.checksum() }

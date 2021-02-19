package com.lightningkite.khrysalis.util

import java.io.File

fun File.checksum(): String {
    return this.readText().hashCode().toString()
}
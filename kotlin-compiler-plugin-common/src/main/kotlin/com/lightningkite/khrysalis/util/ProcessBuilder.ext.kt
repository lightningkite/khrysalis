package com.lightningkite.khrysalis.util

import java.io.File

fun Process.correctedFileOutput(file: File): Process {
    this.inputStream.use { i -> file.outputStream().use { o -> i.copyTo(o) } }
    return this
}
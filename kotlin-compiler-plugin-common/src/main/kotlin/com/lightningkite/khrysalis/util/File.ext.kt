package com.lightningkite.khrysalis.util

import java.io.File

fun File.checksum(): String {
    return this.readText().hashCode().toString()
}

val File.unixPath: String get() = this.path.replace('\\', '/')
val File.unixAbsolutePath: String get() = this.absolutePath.replace('\\', '/')
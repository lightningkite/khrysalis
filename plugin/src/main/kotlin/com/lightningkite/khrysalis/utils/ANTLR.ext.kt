package com.lightningkite.khrysalis.utils

import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.ANTLRInputStream
import java.io.File

fun String.charStream() = ANTLRInputStream(this)
fun File.charStream() = ANTLRFileStream(this.path)

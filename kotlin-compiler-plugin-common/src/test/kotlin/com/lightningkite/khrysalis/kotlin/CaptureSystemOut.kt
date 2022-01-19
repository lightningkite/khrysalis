package com.lightningkite.khrysalis.kotlin

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.StringWriter

inline fun captureSystemOut(action: ()->Unit): String {
    val oldOut = System.out
    val oldErr = System.err
    try {
        val stringStream = ByteArrayOutputStream()
        val stringPrintStream = PrintStream(stringStream)
        System.setOut(stringPrintStream)
        System.setErr(stringPrintStream)
        action()
        stringPrintStream.flush()
        return stringStream.toString(Charsets.UTF_8)
    } finally {
        System.setOut(oldOut)
        System.setErr(oldErr)
    }
}
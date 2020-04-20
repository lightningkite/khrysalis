package com.lightningkite.khrysalis.util

import org.junit.Test

class SmartTabWriterTest {
    @Test
    fun singleBraceTest() {
        println(buildString {
            with(SmartTabWriter(this)) {
                appendln('{')
                append('}')
            }
        })
    }
}
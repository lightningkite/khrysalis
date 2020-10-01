package com.lightningkite.khrysalis.ios.layout2.models

import org.junit.Assert.*
import org.junit.Test

class IosColorTest {
    @Test fun shortAlpha(){
        val color = IosColor.fromHashString("#1234")!!
        assertEquals(1f/15f, color.alpha)
        assertEquals(2f/15f, color.red)
        assertEquals(3f/15f, color.green)
        assertEquals(4f/15f, color.blue)
    }
}
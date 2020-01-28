package com.lightningkite.kwift.bytes

import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteBuffer_actualKtTest {
    @Test fun testWrite(){
        val out = ByteBuffer.allocate(4).putInt(1852935).data()
        println(out.joinToString { it.toString(16) })
    }
}

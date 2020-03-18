package com.lightningkite.khrysalis.flow

import com.lightningkite.khrysalis.utils.checksum
import org.junit.Test

class StringChecksumTest {
    @Test fun test(){
        val front = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm"
        val items = listOf(
            "a $front ending",
            "b $front ending",
            "a $front ending2"
        )
        assert(items.map { it.checksum() }.distinct().size == items.size)
    }
}

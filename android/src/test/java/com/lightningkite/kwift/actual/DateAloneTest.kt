package com.lightningkite.kwift.actual

import org.junit.Assert.assertEquals
import org.junit.Test

class DateAloneTest {
    @Test
    fun test() {
        val date = DateAlone(2019, 12, 4)
        val stamp = dateFrom(date, TimeAlone(12, 0, 0))
        println(stamp.format(ClockPartSize.Full, ClockPartSize.None))
        val cycled = stamp.dateAlone
        assertEquals(date, cycled)
    }
}
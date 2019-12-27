package com.lightningkite.kwift.actual

import com.lightningkite.kwift.shared.ClockPartSize
import com.lightningkite.kwift.shared.DateAlone
import com.lightningkite.kwift.shared.TimeAlone
import org.junit.Assert.assertEquals
import org.junit.Test

class DateAloneTest {
    @Test
    fun test() {
        val date = DateAlone(2019, 12, 4)
        val stamp = dateFrom(date, TimeAlone(12, 0, 0))
        println(stamp.format(com.lightningkite.kwift.shared.ClockPartSize.Full, ClockPartSize.None))
        val cycled = stamp.dateAlone
        assertEquals(date, cycled)
    }
}

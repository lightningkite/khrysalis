package com.lightningkite.kwift.actual

import com.lightningkite.kwift.shared.ClockPartSize
import com.lightningkite.kwift.shared.DateAlone
import com.lightningkite.kwift.shared.TimeAlone
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class DateAloneTest {
    @Test
    fun test() {
        val date = DateAlone(2019, 12, 4)
        val stamp = dateFrom(date, TimeAlone(12, 0, 0))
        println(stamp.format(com.lightningkite.kwift.shared.ClockPartSize.Full, ClockPartSize.None))
        val cycled = stamp.dateAlone
        assertEquals(date, cycled)
    }

    @Test fun createFromMonthInEra(){
        val dates = generateSequence(Date().dateAlone) { it.setAddDayOfMonth(1) }.take(400)
        for(date in dates){
            assertEquals("Failed for ${date.iso8601()}", date.monthInEra, DateAlone.fromMonthInEra(date.monthInEra).monthInEra)
        }
    }
}

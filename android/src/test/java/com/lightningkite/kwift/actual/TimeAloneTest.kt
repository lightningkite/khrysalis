package com.lightningkite.kwift.actual

import com.lightningkite.kwift.time.TimeAlone
import junit.framework.Assert.assertEquals
import org.junit.Test

class TimeAloneTest {

    @Test
    fun test() {
        var time1 = TimeAlone(20, 20, 20)
        var time2 = TimeAlone(10, 10, 10)

        var result = time1 - time2
        assertEquals(10, result.hour)
        assertEquals(10, result.minute)
        assertEquals(10, result.second)

        time1 = TimeAlone(9, 20, 20)
        result = time1 - time2
        assertEquals(0, result.hour)
        assertEquals(0, result.minute)
        assertEquals(0, result.second)

        time1 = TimeAlone(20, 20, 0)
        result = time1 - time2
        assertEquals(50, result.second)
        assertEquals(9, result.minute)
        assertEquals(10, result.hour)

        time1 = TimeAlone(20, 0, 20)
        result = time1 - time2
        assertEquals(9, result.hour)
        assertEquals(50, result.minute)
        assertEquals(10, result.second)

        time1 = TimeAlone(20, 0, 0)
        result = time1 - time2
        assertEquals(9, result.hour)
        assertEquals(49, result.minute)
        assertEquals(50, result.second)

        time1 = TimeAlone(20, 9, 9)
        result = time1 - time2
        assertEquals(9, result.hour)
        assertEquals(58, result.minute)
        assertEquals(59, result.second)

        time1 = TimeAlone(58, 59, 60)
        time2 = TimeAlone(58, 59, 60)
        result = time1 - time2

        assertEquals(0, result.second)
        assertEquals(0, result.minute)
        assertEquals(0, result.hour)

        time1 = TimeAlone(59, 59, 59)
        time2 = TimeAlone(58, 59, 60)
        result = time1 - time2

        assertEquals(59, result.second)
        assertEquals(59, result.minute)
        assertEquals(0, result.hour)

        time1 = TimeAlone(59, 59, 59)
        time2 = TimeAlone(58, 58, 58)
        result = time1 - time2

        assertEquals(1, result.second)
        assertEquals(1, result.minute)
        assertEquals(1, result.hour)

        time1 = TimeAlone(0, 2, 0)
        time2 = TimeAlone(0, 0, 120)
        result = time1 - time2

        assertEquals(0, result.second)
        assertEquals(0, result.minute)
        assertEquals(0, result.hour)

        time1 = TimeAlone(2, 0, 0)
        time2 = TimeAlone(0, 120, 0)
        result = time1 - time2

        assertEquals(0, result.second)
        assertEquals(0, result.minute)
        assertEquals(0, result.hour)

        time1 = TimeAlone(0, 2, 0)
        time2 = TimeAlone(0, 0, 121)
        result = time1 - time2

        assertEquals(0, result.second)
        assertEquals(0, result.minute)
        assertEquals(0, result.hour)
    }

    @Test fun hoursTest(){
        for(second in 0..24*60*60){
            val time = TimeAlone(0, 0, 0)
            time.secondsInDay = second
            val before = time.comparable
            time.hoursInDay = time.hoursInDay
            val after = time.comparable
            assertEquals(before, after)
        }
    }
}

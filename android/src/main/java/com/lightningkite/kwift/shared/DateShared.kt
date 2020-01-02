package com.lightningkite.kwift.shared

import com.lightningkite.kwift.actual.*

import java.util.*

data class DateAlone(var year: Int, var month: Int, var day: Int) {
    companion object {
        fun now(): DateAlone = Date().dateAlone
        val farPast = DateAlone(-99999, 1, 1)
        val farFuture = DateAlone(99999, 12, 31)
        fun iso(string: String): DateAlone = DateAlone(
            string.substringBefore("-").toInt(),
            string.substringAfter("-").substringBefore("-").toInt(),
            string.substringAfterLast("-").toInt()
        )
        fun fromMonthInEra(monthInEra: Int): DateAlone {
            return DateAlone(
                year = (monthInEra-1) / 12,
                month = (monthInEra-1) % 12 + 1,
                day = 1
            )
        }
    }
    val monthInEra: Int get() = this.year * 12 + this.month
    val comparable: Int get() = this.year * 12 * 31 + this.month * 31 + this.day
    val dayOfWeek: Int get() = dateFrom(this, TimeAlone.noon).dayOfWeek
    fun set(other: DateAlone): DateAlone {
        this.year = other.year
        this.month = other.month
        this.day = other.day
        return this
    }
}

data class TimeAlone(var hour: Int, var minute: Int, var second: Int) {
    companion object {
        fun now(): TimeAlone = Date().timeAlone
        fun iso(string: String): TimeAlone = TimeAlone(
            string.substringBefore(":").toInt(),
            string.substringAfter(":").substringBefore(":").toInt(),
            string.substringAfterLast(":").toInt()
        )

        val min = TimeAlone(0, 0, 0)
        val midnight = min
        val noon = TimeAlone(12, 0, 0)
        val max = TimeAlone(23, 59, 59)
    }

    val comparable: Int get() = this.hour * 60 * 60 + this.minute * 60 + this.second
    val secondsInDay: Int get() = this.hour * 60 * 60 + this.minute * 60 + this.second

    fun normalize() {
        hour = (hour + minute.floorDiv(60)).floorMod(24)
        minute = (minute + second.floorDiv(60)).floorMod(60)
        second = second.floorMod(60)
    }
    fun set(other: TimeAlone): TimeAlone {
        this.hour = other.hour
        this.minute = other.minute
        this.second = other.second
        return this
    }
}

enum class ClockPartSize { None, Short, Medium, Long, Full }

fun DateAlone.format(clockPartSize: ClockPartSize): String = dateFrom(this, TimeAlone.noon).format(clockPartSize, ClockPartSize.None)
fun TimeAlone.format(clockPartSize: ClockPartSize): String = dateFrom(Date().dateAlone, this).format(ClockPartSize.None, clockPartSize)

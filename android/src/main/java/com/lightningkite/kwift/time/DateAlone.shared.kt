package com.lightningkite.kwift.time

import java.util.*

data class DateAlone(var year: Int, var month: Int, var day: Int) {
    companion object {
        fun now(): DateAlone = Date().dateAlone
        val farPast = DateAlone(-99999, 1, 1)
        val farFuture = DateAlone(99999, 12, 31)
        fun iso(string: String): DateAlone =
            DateAlone(
                string.substringBefore("-").toInt(),
                string.substringAfter("-").substringBefore("-").toInt(),
                string.substringAfterLast("-").toInt()
            )
        fun fromMonthInEra(monthInEra: Int): DateAlone {
            return DateAlone(
                year = (monthInEra - 1) / 12,
                month = (monthInEra - 1) % 12 + 1,
                day = 1
            )
        }
    }
    val monthInEra: Int get() = this.year * 12 + this.month
    val comparable: Int get() = this.year * 12 * 31 + this.month * 31 + this.day
    val dayOfWeek: Int get() = dateFrom(
        this,
        TimeAlone.noon
    ).dayOfWeek
    fun set(other: DateAlone): DateAlone {
        this.year = other.year
        this.month = other.month
        this.day = other.day
        return this
    }
}

fun DateAlone.format(clockPartSize: ClockPartSize): String = dateFrom(
    this,
    TimeAlone.noon
).format(clockPartSize, ClockPartSize.None)

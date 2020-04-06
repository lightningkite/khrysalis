package com.lightningkite.khrysalis.time

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * A class to hold the current Year, Month, and day. Can be created from a Date object Date().dateAlone
 * Has a full set of Methods and and variables for manipulating.
 *
 */

data class DateAlone(var year: Int, var month: Int, var day: Int) {
    companion object {
        fun now(): DateAlone = Date().dateAlone
        val farPast = DateAlone(-99999, 1, 1)
        val farFuture = DateAlone(99999, 12, 31)
        fun iso(string: String): DateAlone? {
            return DateAlone(
                string.substringBefore("-", "").toIntOrNull() ?: return null,
                string.substringAfter("-", "").substringBefore("-", "").toIntOrNull() ?: return null,
                string.substringAfterLast("-", "").toIntOrNull() ?: return null
            )
        }
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
}

fun DateAlone.setDayOfMonth(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setDayOfMonth(value); set(
            date
        )
        }
    }

fun DateAlone.setMonthOfYear(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setMonthOfYear(value); set(
            date
        )
        }
    }

fun DateAlone.setYearAd(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setYearAd(value); set(
            date
        )
        }
    }

fun DateAlone.setDayOfWeek(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setDayOfWeek(value); set(
            date
        )
        }
    }

fun DateAlone.setAddDayOfWeek(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setAddDayOfWeek(value); set(
            date
        )
        }
    }

fun DateAlone.setAddDayOfMonth(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setAddDayOfMonth(
            value
        ); set(date)
        }
    }

fun DateAlone.setAddMonthOfYear(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setAddMonthOfYear(
            value
        ); set(date)
        }
    }

fun DateAlone.setAddYearAd(value: Int): DateAlone =
    apply {
        useDate { date ->
            date.set(this); date.setAddYearAd(value); set(
            date
        )
        }
    }


fun DateAlone.dayOfMonth(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setDayOfMonth(value); date.dateAlone }
fun DateAlone.monthOfYear(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setMonthOfYear(value); date.dateAlone }
fun DateAlone.yearAd(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setYearAd(value); date.dateAlone }
fun DateAlone.dayOfWeek(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setDayOfWeek(value); date.dateAlone }
fun DateAlone.addDayOfWeek(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setAddDayOfWeek(value); date.dateAlone }
fun DateAlone.addDayOfMonth(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setAddDayOfMonth(value); date.dateAlone }
fun DateAlone.addMonthOfYear(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setAddMonthOfYear(value); date.dateAlone }
fun DateAlone.addYearAd(value: Int): DateAlone =
    useDate { date -> date.set(this); date.setAddYearAd(value); date.dateAlone }

@SuppressLint("SimpleDateFormat")
fun DateAlone.iso8601(): String = SimpleDateFormat("yyyy-MM-dd").format(
    dateFrom(
        this,
        TimeAlone.noon
    )
)

@SuppressLint("SimpleDateFormat")
fun DateAlone.formatYearless(clockPartSize: ClockPartSize): String {
    return when(clockPartSize){
        ClockPartSize.None -> ""
        ClockPartSize.Short -> SimpleDateFormat("MMM d").format(dateFrom(this, TimeAlone.noon))
        ClockPartSize.Medium -> SimpleDateFormat("MMMM d").format(dateFrom(this, TimeAlone.noon))
        ClockPartSize.Long -> SimpleDateFormat("EEE MMM d").format(dateFrom(this, TimeAlone.noon))
        ClockPartSize.Full -> SimpleDateFormat("EEEE MMMM d").format(dateFrom(this, TimeAlone.noon))
    }
}

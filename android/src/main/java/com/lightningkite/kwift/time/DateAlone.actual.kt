package com.lightningkite.kwift.time

import java.text.SimpleDateFormat


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

fun DateAlone.iso8601(): String = SimpleDateFormat("yyyy-MM-dd").format(
    dateFrom(
        this,
        TimeAlone.noon
    )
)

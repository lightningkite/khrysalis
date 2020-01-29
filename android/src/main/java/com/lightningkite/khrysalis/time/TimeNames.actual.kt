package com.lightningkite.khrysalis.time

import java.text.DateFormatSymbols

object TimeNames {
    private val symbols = DateFormatSymbols()
    val shortMonthNames: List<String> = symbols.shortMonths.toList()
    val monthNames: List<String> = symbols.months.toList()
    val shortWeekdayNames: List<String> = symbols.shortWeekdays.toList().drop(1)
    val weekdayNames: List<String> = symbols.weekdays.toList().drop(1)
    fun shortMonthName(oneIndexedPosition: Int): String = shortMonthNames[oneIndexedPosition - 1]
    fun monthName(oneIndexedPosition: Int): String = monthNames[oneIndexedPosition - 1]
    fun shortWeekdayName(oneIndexedPosition: Int): String = shortWeekdayNames[oneIndexedPosition - 1]
    fun weekdayName(oneIndexedPosition: Int): String = weekdayNames[oneIndexedPosition - 1]
}

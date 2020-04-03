package com.lightningkite.khrysalis.time

import java.text.DateFormatSymbols

/**
 *
 * Each function returns a list of strings of the names of weekdays, and months,
 * or a single string for the specific weekday or month
 *
 * Example : ["Sunday", "Monday", "Tuesday", ...]
 * Example : ["Sun", "Mon", "Tue", ...]
 *
 */

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

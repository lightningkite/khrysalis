package com.lightningkite.kwift.actual

data class DateAlone(val year: Int, val month: Int, val day: Int) {
    fun iso(): String = "$year-$month-$day"
    companion object {
        fun iso(string: String): DateAlone = DateAlone(
            string.substringBefore("-").toInt(),
            string.substringAfter("-").substringBefore("-").toInt(),
            string.substringAfterLast("-").toInt()
        )
    }
}

data class TimeAlone(val hour: Int, val minute: Int, val second: Int) {
    fun iso(): String = "$hour:$minute:$second"
    companion object {
        fun iso(string: String): TimeAlone = TimeAlone(
            string.substringBefore(":").toInt(),
            string.substringAfter(":").substringBefore(":").toInt(),
            string.substringAfterLast(":").toInt()
        )
    }
}

enum class ClockPartSize { None, Short, Medium, Long, Full }
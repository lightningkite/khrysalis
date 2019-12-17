package com.lightningkite.kwift.actual

data class DateAlone(val year: Int, val month: Int, val day: Int) {
    companion object {
        fun iso(string: String): DateAlone = DateAlone(
            string.substringBefore("-").toInt(),
            string.substringAfter("-").substringBefore("-").toInt(),
            string.substringAfterLast("-").toInt()
        )
    }
}

data class TimeAlone(val hour: Int, val minute: Int, val second: Int): Comparable<TimeAlone> {
    companion object {
        fun iso(string: String): TimeAlone = TimeAlone(
            string.substringBefore(":").toInt(),
            string.substringAfter(":").substringBefore(":").toInt(),
            string.substringAfterLast(":").toInt()
        )
    }

    override fun compareTo(other: TimeAlone): Int {
        return (this.hour * 60 * 60 + this.minute * 60 + this.second) - (other.hour * 60 * 60 + other.minute * 60 + other.second)
    }


}

enum class ClockPartSize { None, Short, Medium, Long, Full }

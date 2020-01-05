package com.lightningkite.kwift.time

import com.lightningkite.kwift.floorDiv
import com.lightningkite.kwift.floorMod
import java.text.SimpleDateFormat
import java.util.*

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
    var secondsInDay: Int
        get() = this.hour * 60 * 60 + this.minute * 60 + this.second
        set(value) {
            this.hour = value / 60 / 60
            this.minute = value / 60 % 60
            this.second = value % 60
        }
    var hoursInDay: Float
        get() = this.hour.toFloat() + this.minute.toFloat() / 60f + this.second.toFloat() / 3600f + 0.5f/3600f
        set(value) {
            this.hour = value.toInt()
            this.minute = (value * 60f).toInt() % 60
            this.second = (value * 3600f).toInt() % 60
        }

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

fun TimeAlone.format(clockPartSize: ClockPartSize): String = dateFrom(
    Date().dateAlone,
    this
).format(ClockPartSize.None, clockPartSize)

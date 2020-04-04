package com.lightningkite.khrysalis.time

import java.text.SimpleDateFormat
import java.util.*

data class TimeAlone(var hour: Int, var minute: Int, var second: Int) {
    companion object {
        fun now(): TimeAlone = Date().timeAlone
        fun iso(string: String): TimeAlone? {
            return TimeAlone(
                string.substringBefore(":", "").toIntOrNull() ?: return null,
                string.substringAfter(":", "").substringBefore(":", "").toIntOrNull() ?: return null,
                string.substringAfter(":", "").substringAfter(":", "").toIntOrNull() ?: 0
            )
        }

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
}

fun TimeAlone.iso8601(): String = SimpleDateFormat("HH:mm:ss").format(
    dateFrom(Date().dateAlone, this)
)

operator fun TimeAlone.minus(rhs: TimeAlone): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) - (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second)
    return if (result < 0) {
        TimeAlone(0, 0, 0)
    } else {
        TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
    }
}

operator fun TimeAlone.plus(rhs: TimeAlone): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) + (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second)
    return TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
}

operator fun TimeAlone.minus(rhs: TimeInterval): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) - rhs.milliseconds.toInt() / 1000
    return if (result < 0) {
        TimeAlone(0, 0, 0)
    } else {
        TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
    }
}

operator fun TimeAlone.plus(rhs: TimeInterval): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) + rhs.milliseconds.toInt() / 1000
    return TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
}

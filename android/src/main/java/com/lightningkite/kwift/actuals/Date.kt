package com.lightningkite.kwift.actuals

import java.util.*

inline class TimeInterval(val milliseconds: Long) {
    val seconds: Double get() = milliseconds / 1000.0
}

fun Int.milliseconds(): TimeInterval = TimeInterval(this.toLong())
fun Int.seconds(): TimeInterval = TimeInterval(this.toLong() * 1000L)
fun Int.minutes(): TimeInterval = TimeInterval(this.toLong() * 60L * 1000L)
fun Int.hours(): TimeInterval = TimeInterval(this.toLong() * 60L * 60L * 1000L)
fun Int.days(): TimeInterval = TimeInterval(this.toLong() * 24L * 60L * 60L * 1000L)

operator fun Date.plus(interval: TimeInterval): Date = Date(time + interval.milliseconds)
operator fun Date.minus(interval: TimeInterval): Date = Date(time - interval.milliseconds)

/**One Indexed**/
val Date.dayOfWeek: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.DAY_OF_WEEK)
/**One Indexed**/
val Date.dayOfMonth: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.DAY_OF_MONTH)
/**One Indexed**/
val Date.monthOfYear: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.MONTH) + 1
val Date.yearAd: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.YEAR)
val Date.hourOfDay: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.HOUR_OF_DAY)
val Date.minuteOfHour: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.MINUTE)
val Date.secondOfMinute: Int get() = Calendar.getInstance().also { it.timeInMillis = time }.get(Calendar.SECOND)

fun Date.sameDay(other: Date): Boolean {
    return this.yearAd == other.yearAd && this.monthOfYear == other.monthOfYear && this.dayOfMonth == other.dayOfMonth
}

fun Date.sameMonth(other: Date): Boolean {
    return this.yearAd == other.yearAd && this.monthOfYear == other.monthOfYear
}

fun Date.sameYear(other: Date): Boolean {
    return this.yearAd == other.yearAd
}

/**One Indexed**/
fun Date.dayOfWeek(value: Int) =
    Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.DAY_OF_WEEK, value) }.time

/**One Indexed**/
fun Date.dayOfMonth(value: Int) =
    Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.DAY_OF_MONTH, value) }.time

/**One Indexed**/
fun Date.monthOfYear(value: Int) =
    Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.MONTH, value - 1) }.time

fun Date.yearAd(value: Int) = Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.YEAR, value) }.time
fun Date.hourOfDay(value: Int) =
    Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.HOUR_OF_DAY, value) }.time

fun Date.minuteOfHour(value: Int) =
    Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.MINUTE, value) }.time

fun Date.secondOfMinute(value: Int) =
    Calendar.getInstance().also { it.timeInMillis = time; it.set(Calendar.SECOND, value) }.time

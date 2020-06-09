package com.lightningkite.khrysalis.time



inline class TimeInterval(val milliseconds: Long) {
    val seconds: Double get() = milliseconds / 1000.0
    val minutes: Double get() = seconds / 60.0
    val hours: Double get() = minutes / 60.0
    val days: Double get() = hours / 24.0
    val approximateYears: Double get() = days / 365.25
}

fun Long.milliseconds(): TimeInterval =
    TimeInterval(this.toLong())
fun Int.milliseconds(): TimeInterval =
    TimeInterval(this.toLong())
fun Int.seconds(): TimeInterval =
    TimeInterval(this.toLong() * 1000L)
fun Int.minutes(): TimeInterval =
    TimeInterval(this.toLong() * 60L * 1000L)
fun Int.hours(): TimeInterval =
    TimeInterval(this.toLong() * 60L * 60L * 1000L)
fun Int.days(): TimeInterval =
    TimeInterval(this.toLong() * 24L * 60L * 60L * 1000L)

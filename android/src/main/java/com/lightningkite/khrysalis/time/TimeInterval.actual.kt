package com.lightningkite.khrysalis.time



inline class TimeInterval(val milliseconds: Long) {
    val seconds: Double get() = milliseconds / 1000.0
}

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

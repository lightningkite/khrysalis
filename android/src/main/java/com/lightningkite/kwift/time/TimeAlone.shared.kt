package com.lightningkite.kwift.time

import com.lightningkite.kwift.floorDiv
import com.lightningkite.kwift.floorMod
import java.text.SimpleDateFormat
import java.util.*


fun TimeAlone.normalize() {
    hour = (hour + minute.floorDiv(60)).floorMod(24)
    minute = (minute + second.floorDiv(60)).floorMod(60)
    second = second.floorMod(60)
}

fun TimeAlone.set(other: TimeAlone): TimeAlone {
    this.hour = other.hour
    this.minute = other.minute
    this.second = other.second
    return this
}


fun TimeAlone.format(clockPartSize: ClockPartSize): String = dateFrom(
    Date().dateAlone,
    this
).format(ClockPartSize.None, clockPartSize)

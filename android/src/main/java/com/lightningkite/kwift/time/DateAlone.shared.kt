package com.lightningkite.kwift.time

import java.util.*

fun DateAlone.set(other: DateAlone): DateAlone {
    this.year = other.year
    this.month = other.month
    this.day = other.day
    return this
}

fun DateAlone.format(clockPartSize: ClockPartSize): String = dateFrom(
    this,
    TimeAlone.noon
).format(clockPartSize, ClockPartSize.None)

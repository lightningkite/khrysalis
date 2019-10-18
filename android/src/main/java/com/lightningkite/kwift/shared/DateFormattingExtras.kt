package com.lightningkite.kwift.shared

import com.lightningkite.kwift.actual.*
import java.util.*

fun DateAlone.format(clockPartSize: ClockPartSize): String
        = dateFrom(this, TimeAlone(0, 0, 0)).format(clockPartSize, ClockPartSize.None)
fun TimeAlone.format(clockPartSize: ClockPartSize): String
        = dateFrom(Date().dateAlone, this).format(ClockPartSize.None, clockPartSize)

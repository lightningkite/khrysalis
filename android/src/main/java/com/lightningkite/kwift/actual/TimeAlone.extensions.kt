package com.lightningkite.kwift.actual

import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

fun TimeAlone.iso8601(): String = SimpleDateFormat("HH:mm:ss").format(dateFrom(Date().dateAlone, this))

operator fun TimeAlone.minus(rhs: TimeAlone): TimeAlone {
    val result = (this.hour * 60 * 60 + this.minute * 60 + this.second) - (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second)
    return if(result < 0){
        TimeAlone(0,0,0)
    }else{
        TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
    }
}

operator fun TimeAlone.plus(rhs:TimeAlone):TimeAlone{
    val result = (this.hour * 60 * 60 + this.minute * 60 + this.second) + (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second)
    return TimeAlone(result / 60 / 60,  result / 60 % 60, result % 60)
}


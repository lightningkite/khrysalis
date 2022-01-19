@file:SharedCode
package com.test.lambda

import com.lightningkite.khrysalis.*
import java.time.*
import java.time.format.*

fun main(){
    val date = LocalDate.of(2021, 5, 5)
    println(date.year)
    println(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    val time = LocalTime.of(12, 0, 0)
    println(time.hour)
    println(time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)))
    val dateTime = LocalDateTime.of(2021, 5, 5, 12, 0, 0)
    println(dateTime.year)
    println(dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)))
}
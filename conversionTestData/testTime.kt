@file:SharedCode
package com.test.time

import com.lightningkite.khrysalis.*
import java.time.*
import java.time.format.*

fun main(){
    val date = LocalDate.of(2021, 5, 5)
    println(date.year)
    println(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    println(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(date))
    val time = LocalTime.of(12, 0, 0)
    println(time.hour)
    println(time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)))
    println(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).format(time))
    val dateTime = LocalDateTime.of(2021, 5, 5, 12, 0, 0)
    println(dateTime.year)
    dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT))
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT).format(dateTime)
    val zoned = ZonedDateTime.of(dateTime, ZoneId.of("UTC"))

    // This will work, but let's not rely on specific time zones for the output equality check.
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).format(zoned)

    var instant = Instant.ofEpochSecond(100000L)
    instant = instant.plusMillis(2000)
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)
        .withZone(ZoneId.of("UTC"))
        .format(instant)

    ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))
}
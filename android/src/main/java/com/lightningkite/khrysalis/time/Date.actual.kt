package com.lightningkite.khrysalis.time

import com.fasterxml.jackson.databind.util.StdDateFormat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

internal val calendarPool = ConcurrentLinkedQueue<Calendar>()
internal inline fun <T> useCalendar(action: (Calendar) -> T): T {
    val cal = calendarPool.poll() ?: Calendar.getInstance()
    val result = action(cal)
    calendarPool.offer(cal)
    return result
}

internal val datePool = ConcurrentLinkedQueue<Date>()
internal inline fun <T> useDate(action: (Date) -> T): T {
    val cal = datePool.poll() ?: Date()
    val result = action(cal)
    datePool.offer(cal)
    return result
}

operator fun Date.plus(interval: TimeInterval): Date = Date(time + interval.milliseconds)
operator fun Date.minus(interval: TimeInterval): Date = Date(time - interval.milliseconds)

/**One Indexed**/
val Date.dayOfWeek: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.DAY_OF_WEEK)
}
/**One Indexed**/
val Date.dayOfMonth: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.DAY_OF_MONTH)
}
/**One Indexed**/
val Date.monthOfYear: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.MONTH) + 1
}
val Date.yearAd: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.YEAR)
}
val Date.hourOfDay: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.HOUR_OF_DAY)
}
val Date.minuteOfHour: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.MINUTE)
}
val Date.secondOfMinute: Int get() = useCalendar {
    it.timeInMillis = time; it.get(Calendar.SECOND)
}

val Date.dateAlone: DateAlone
    get() {
        return useCalendar { cal ->
            cal.timeInMillis = time
            DateAlone(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }
    }
val Date.timeAlone: TimeAlone
    get() {
        return useCalendar { cal ->
            cal.timeInMillis = time
            TimeAlone(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND)
            )
        }
    }

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
fun Date.dayOfWeek(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.DAY_OF_WEEK, value)
        existing.time = it.timeInMillis
        existing
    }

/**One Indexed**/
fun Date.dayOfMonth(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.DAY_OF_MONTH, value)
        existing.time = it.timeInMillis
        existing
    }

/**One Indexed**/
fun Date.monthOfYear(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.MONTH, value - 1)
        existing.time = it.timeInMillis
        existing
    }

fun Date.yearAd(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.YEAR, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.hourOfDay(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.HOUR_OF_DAY, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.minuteOfHour(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.MINUTE, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.secondOfMinute(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.SECOND, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addDayOfWeek(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.DAY_OF_WEEK, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addDayOfMonth(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.DAY_OF_MONTH, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addMonthOfYear(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.MONTH, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addYearAd(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.YEAR, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addHourOfDay(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.HOUR_OF_DAY, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addMinuteOfHour(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.MINUTE, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.addSecondOfMinute(value: Int, existing: Date = Date()) =
    useCalendar {
        it.timeInMillis = time
        it.add(Calendar.SECOND, value)
        existing.time = it.timeInMillis
        existing
    }

fun Date.setDayOfWeek(value: Int) = this.dayOfWeek(value, existing = this)
fun Date.setDayOfMonth(value: Int) = this.dayOfMonth(value, existing = this)
fun Date.setMonthOfYear(value: Int) = this.monthOfYear(value, existing = this)
fun Date.setYearAd(value: Int) = this.yearAd(value, existing = this)
fun Date.setHourOfDay(value: Int) = this.hourOfDay(value, existing = this)
fun Date.setMinuteOfHour(value: Int) = this.minuteOfHour(value, existing = this)
fun Date.setSecondOfMinute(value: Int) = this.secondOfMinute(value, existing = this)
fun Date.setAddDayOfWeek(value: Int) = this.addDayOfWeek(value, existing = this)
fun Date.setAddDayOfMonth(value: Int) = this.addDayOfMonth(value, existing = this)
fun Date.setAddMonthOfYear(value: Int) = this.addMonthOfYear(value, existing = this)
fun Date.setAddYearAd(value: Int) = this.addYearAd(value, existing = this)
fun Date.setAddHourOfDay(value: Int) = this.addHourOfDay(value, existing = this)
fun Date.setAddMinuteOfHour(value: Int) = this.addMinuteOfHour(value, existing = this)
fun Date.setAddSecondOfMinute(value: Int) = this.addSecondOfMinute(value, existing = this)

fun Date.set(dateAlone: DateAlone): Date {
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.YEAR, dateAlone.year)
        it.set(Calendar.MONTH, dateAlone.month - 1)
        it.set(Calendar.DAY_OF_MONTH, dateAlone.day)
        this.time = it.timeInMillis
    }
    return this
}

fun Date.set(timeAlone: TimeAlone): Date {
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.HOUR_OF_DAY, timeAlone.hour)
        it.set(Calendar.MINUTE, timeAlone.minute)
        it.set(Calendar.SECOND, timeAlone.second)
        this.time = it.timeInMillis
    }
    return this
}

fun Date.set(dateAlone: DateAlone, timeAlone: TimeAlone): Date {
    useCalendar {
        it.timeInMillis = time
        it.set(Calendar.YEAR, dateAlone.year)
        it.set(Calendar.MONTH, dateAlone.month - 1)
        it.set(Calendar.DAY_OF_MONTH, dateAlone.day)
        it.set(Calendar.HOUR_OF_DAY, timeAlone.hour)
        it.set(Calendar.MINUTE, timeAlone.minute)
        it.set(Calendar.SECOND, timeAlone.second)
        this.time = it.timeInMillis
    }
    return this
}

fun DateAlone.set(date: Date): DateAlone {
    useCalendar {
        it.timeInMillis = date.time
        year = it.get(Calendar.YEAR)
        month = it.get(Calendar.MONTH) + 1
        day = it.get(Calendar.DAY_OF_MONTH)
    }
    return this
}

fun TimeAlone.set(date: Date): TimeAlone {
    useCalendar {
        it.timeInMillis = date.time
        hour = it.get(Calendar.HOUR_OF_DAY)
        minute = it.get(Calendar.MINUTE)
        second = it.get(Calendar.SECOND)
    }
    return this
}

fun dateFrom(dateAlone: DateAlone, timeAlone: TimeAlone, existing: Date = Date()): Date {
    return useCalendar {
        it.set(Calendar.YEAR, dateAlone.year)
        it.set(Calendar.MONTH, dateAlone.month - 1)
        it.set(Calendar.DAY_OF_MONTH, dateAlone.day)
        it.set(Calendar.HOUR_OF_DAY, timeAlone.hour)
        it.set(Calendar.MINUTE, timeAlone.minute)
        it.set(Calendar.SECOND, timeAlone.second)
        existing.time = it.timeInMillis
        existing
    }
}

fun dateFromIso(iso8601: String): Date? {
    return StdDateFormat().withLenient(true).parse(iso8601)
}

fun Date.format(dateStyle: ClockPartSize, timeStyle: ClockPartSize): String {
    val rawDateStyle = when (dateStyle) {
        ClockPartSize.None -> DateFormat.SHORT
        ClockPartSize.Short -> DateFormat.SHORT
        ClockPartSize.Medium -> DateFormat.MEDIUM
        ClockPartSize.Long -> DateFormat.LONG
        ClockPartSize.Full -> DateFormat.FULL
    }
    val rawTimeStyle = when (timeStyle) {
        ClockPartSize.None -> DateFormat.SHORT
        ClockPartSize.Short -> DateFormat.SHORT
        ClockPartSize.Medium -> DateFormat.MEDIUM
        ClockPartSize.Long -> DateFormat.LONG
        ClockPartSize.Full -> DateFormat.FULL
    }

    val format = if (dateStyle == ClockPartSize.None) {
        if (timeStyle == ClockPartSize.None) {
            throw IllegalStateException()
        }
        DateFormat.getTimeInstance(rawTimeStyle)
    } else if (timeStyle == ClockPartSize.None) {
        DateFormat.getDateInstance(rawDateStyle)
    } else {
        DateFormat.getDateTimeInstance(rawDateStyle, rawTimeStyle)
    }
    return format.format(this)
}

fun Date.iso8601(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("UTC") }.format(this)

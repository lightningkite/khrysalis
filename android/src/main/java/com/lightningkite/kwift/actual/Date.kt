package com.lightningkite.kwift.actual

import com.lightningkite.kwift.shared.ClockPartSize
import com.lightningkite.kwift.shared.DateAlone
import com.lightningkite.kwift.shared.TimeAlone
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@Deprecated("Use from shared package instead", ReplaceWith("TimeAlone", "com.lightningkite.kwift.shared.TimeAlone"))
typealias TimeAlone = com.lightningkite.kwift.shared.TimeAlone

@Deprecated("Use from shared package instead", ReplaceWith("DateAlone", "com.lightningkite.kwift.shared.DateAlone"))
typealias DateAlone = com.lightningkite.kwift.shared.DateAlone

@Deprecated(
    "Use from shared package instead",
    ReplaceWith("ClockPartSize", "com.lightningkite.kwift.shared.ClockPartSize")
)
typealias ClockPartSize = com.lightningkite.kwift.shared.ClockPartSize

inline class TimeInterval(val milliseconds: Long) {
    val seconds: Double get() = milliseconds / 1000.0
}

private val calendarPool = ConcurrentLinkedQueue<Calendar>()
private inline fun <T> useCalendar(action: (Calendar) -> T): T {
    val cal = calendarPool.poll() ?: Calendar.getInstance()
    val result = action(cal)
    calendarPool.offer(cal)
    return result
}

private val datePool = ConcurrentLinkedQueue<Date>()
private inline fun <T> useDate(action: (Date) -> T): T {
    val cal = datePool.poll() ?: Date()
    val result = action(cal)
    datePool.offer(cal)
    return result
}

fun Int.milliseconds(): TimeInterval = TimeInterval(this.toLong())
fun Int.seconds(): TimeInterval = TimeInterval(this.toLong() * 1000L)
fun Int.minutes(): TimeInterval = TimeInterval(this.toLong() * 60L * 1000L)
fun Int.hours(): TimeInterval = TimeInterval(this.toLong() * 60L * 60L * 1000L)
fun Int.days(): TimeInterval = TimeInterval(this.toLong() * 24L * 60L * 60L * 1000L)

operator fun Date.plus(interval: TimeInterval): Date = Date(time + interval.milliseconds)
operator fun Date.minus(interval: TimeInterval): Date = Date(time - interval.milliseconds)

/**One Indexed**/
val Date.dayOfWeek: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.DAY_OF_WEEK) }
/**One Indexed**/
val Date.dayOfMonth: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.DAY_OF_MONTH) }
/**One Indexed**/
val Date.monthOfYear: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.MONTH) + 1 }
val Date.yearAd: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.YEAR) }
val Date.hourOfDay: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.HOUR_OF_DAY) }
val Date.minuteOfHour: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.MINUTE) }
val Date.secondOfMinute: Int get() = useCalendar { it.timeInMillis = time; it.get(Calendar.SECOND) }

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

fun DateAlone.setDayOfMonth(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setDayOfMonth(value); set(date) } }

fun DateAlone.setMonthOfYear(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setMonthOfYear(value); set(date) } }

fun DateAlone.setYearAd(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setYearAd(value); set(date) } }

fun DateAlone.setDayOfWeek(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setDayOfWeek(value); set(date) } }

fun DateAlone.setAddDayOfWeek(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setAddDayOfWeek(value); set(date) } }

fun DateAlone.setAddDayOfMonth(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setAddDayOfMonth(value); set(date) } }

fun DateAlone.setAddMonthOfYear(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setAddMonthOfYear(value); set(date) } }

fun DateAlone.setAddYearAd(value: Int): DateAlone =
    apply { useDate { date -> date.set(this); date.setAddYearAd(value); set(date) } }


fun DateAlone.dayOfMonth(value: Int): DateAlone = useDate { date -> date.set(this); date.setDayOfMonth(value); date.dateAlone }
fun DateAlone.monthOfYear(value: Int): DateAlone = useDate { date -> date.set(this); date.setMonthOfYear(value); date.dateAlone }
fun DateAlone.yearAd(value: Int): DateAlone = useDate { date -> date.set(this); date.setYearAd(value); date.dateAlone }
fun DateAlone.dayOfWeek(value: Int): DateAlone = useDate { date -> date.set(this); date.setDayOfWeek(value); date.dateAlone }
fun DateAlone.addDayOfWeek(value: Int): DateAlone = useDate { date -> date.set(this); date.setAddDayOfWeek(value); date.dateAlone }
fun DateAlone.addDayOfMonth(value: Int): DateAlone = useDate { date -> date.set(this); date.setAddDayOfMonth(value); date.dateAlone }
fun DateAlone.addMonthOfYear(value: Int): DateAlone = useDate { date -> date.set(this); date.setAddMonthOfYear(value); date.dateAlone }
fun DateAlone.addYearAd(value: Int): DateAlone = useDate { date -> date.set(this); date.setAddYearAd(value); date.dateAlone }

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

fun Date.iso8601(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(this)
fun DateAlone.iso8601(): String = SimpleDateFormat("yyyy-MM-dd").format(dateFrom(this, TimeAlone.noon))
fun TimeAlone.iso8601(): String = SimpleDateFormat("HH:mm:ss").format(dateFrom(Date().dateAlone, this))

object TimeNames {
    private val symbols = DateFormatSymbols()
    val shortMonthNames: List<String> = symbols.shortMonths.toList().dropLast(1)
    val monthNames: List<String> = symbols.months.toList().dropLast(1)
    val shortWeekdayNames: List<String> = symbols.shortWeekdays.toList().drop(1)
    val weekdayNames: List<String> = symbols.weekdays.toList().drop(1)
    fun shortMonthName(oneIndexedPosition: Int): String = shortMonthNames[oneIndexedPosition - 1]
    fun monthName(oneIndexedPosition: Int): String = monthNames[oneIndexedPosition - 1]
    fun shortWeekdayName(oneIndexedPosition: Int): String = shortWeekdayNames[oneIndexedPosition - 1]
    fun weekdayName(oneIndexedPosition: Int): String = weekdayNames[oneIndexedPosition - 1]
}

operator fun TimeAlone.minus(rhs: TimeAlone): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) - (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second)
    return if (result < 0) {
        TimeAlone(0, 0, 0)
    } else {
        TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
    }
}

operator fun TimeAlone.plus(rhs: TimeAlone): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) + (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second)
    return TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
}

operator fun TimeAlone.minus(rhs: TimeInterval): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) - rhs.milliseconds.toInt() / 1000
    return if (result < 0) {
        TimeAlone(0, 0, 0)
    } else {
        TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
    }
}

operator fun TimeAlone.plus(rhs: TimeInterval): TimeAlone {
    val result =
        (this.hour * 60 * 60 + this.minute * 60 + this.second) + rhs.milliseconds.toInt() / 1000
    return TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
}

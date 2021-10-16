package java.time

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.milliseconds

@Deprecated("Use Java 9 time", ReplaceWith("this.toLocalDate()"), level = DeprecationLevel.ERROR)
val ZonedDateTime.dateAlone: LocalDate
    get() = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("this.toLocalTime()"), level = DeprecationLevel.ERROR)
val ZonedDateTime.timeAlone: LocalTime
    get() = throw NotImplementedError()

@Deprecated("Use Java 9 time", ReplaceWith("this.truncatedTo(ChronoUnit.DAYS) == other.truncatedTo(ChronoUnit.DAYS)"), level = DeprecationLevel.ERROR)
fun ZonedDateTime.sameDay(other: ZonedDateTime): Boolean = throw NotImplementedError()

@Deprecated("Use Java 9 time", ReplaceWith("this.truncatedTo(ChronoUnit.MONTHS) == other.truncatedTo(ChronoUnit.MONTHS)"), level = DeprecationLevel.ERROR)
fun ZonedDateTime.sameMonth(other: ZonedDateTime): Boolean = throw NotImplementedError()

@Deprecated("Use Java 9 time", ReplaceWith("this.year == other.year"), level = DeprecationLevel.ERROR)
fun ZonedDateTime.sameYear(other: ZonedDateTime): Boolean = throw NotImplementedError()

@Deprecated("Use Java 9 time", ReplaceWith("ZonedDateTime.of(dateAlone, timeAlone, ZoneId.systemDefault())"), level = DeprecationLevel.ERROR)
fun dateFrom(dateAlone: LocalDate, timeAlone: LocalTime, existing: Date = Date()): ZonedDateTime = throw NotImplementedError()

@Deprecated("Use Java 9 time", ReplaceWith("Instant.parse(iso8601).atZone(ZoneId.systemDefault())"), level = DeprecationLevel.ERROR)
fun dateFromIso(iso8601: String): ZonedDateTime = throw NotImplementedError()

@Deprecated("Prefer Java 9 time")
fun ZonedDateTime.format(dateStyle: FormatStyle?, timeStyle: FormatStyle?): String {
    return if(dateStyle != null) {
        if(timeStyle != null) {
            this.format(DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle))
        } else {
            this.format(DateTimeFormatter.ofLocalizedDate(dateStyle))
        }
    } else {
        if(timeStyle != null) {
            this.format(DateTimeFormatter.ofLocalizedTime(timeStyle))
        } else {
            ""
        }
    }
}

@Deprecated("Use Java 9 time", ReplaceWith("this.format(DateTimeFormatter.ofLocalizedDate(dateStyle))", "java.time.format.DateTimeFormatter"), level = DeprecationLevel.ERROR)
fun LocalDate.format(dateStyle: FormatStyle): String = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("this.format(DateTimeFormatter.ofLocalizedTime(dateStyle))", "java.time.format.DateTimeFormatter"), level = DeprecationLevel.ERROR)
fun LocalTime.format(timeStyle: FormatStyle): String = throw NotImplementedError()

@Deprecated("Use Java 9 time", ReplaceWith("this.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)", "java.time.format.DateTimeFormatter"), level = DeprecationLevel.ERROR)
fun ZonedDateTime.iso8601(): String = throw NotImplementedError()

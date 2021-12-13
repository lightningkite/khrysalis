import {
    convert,
    DateTimeFormatter, Instant,
    LocalDate,
    LocalDateTime, LocalTime, TemporalAccessor, ZonedDateTime
} from "@js-joda/core";

export namespace FormatStyle {
    export const FULL: Intl.DateTimeFormatOptions = {
        weekday: "long",
        era: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit",
        second: "2-digit",
        timeZoneName: "short",
        timeZone: "long",
    }
    export const LONG: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit",
        second: "2-digit",
        timeZoneName: "short",
        timeZone: "long",
    }
    export const MEDIUM: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "numeric",
        second: "2-digit",
        minute: "2-digit"
    }
    export const SHORT: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "numeric",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit"
    }
    export const NONE: Intl.DateTimeFormatOptions = {
    }
}

function altFormat(options: Intl.DateTimeFormatOptions): (temporal: TemporalAccessor) => string {
    return temporal => {
        if(temporal instanceof LocalTime) {
            return convert(LocalDateTime.of(LocalDate.now(), temporal)).toDate().toLocaleTimeString(undefined, options)
        } else if(temporal instanceof LocalDate) {
            return convert(temporal).toDate().toLocaleDateString(undefined, options)
        } else if(temporal instanceof LocalDateTime || temporal instanceof Instant || temporal instanceof ZonedDateTime) {
            return convert(temporal).toDate().toLocaleString(undefined, options)
        } else {
            return ""
        }
    }
}

export function ofLocalizedDate(dateStyle: Intl.DateTimeFormatOptions): DateTimeFormatter {
    const newFormatter = DateTimeFormatter.ofPattern("")
    newFormatter.format = altFormat({
        localeMatcher: dateStyle.localeMatcher,
        weekday: dateStyle.weekday,
        era: dateStyle.era,
        year: dateStyle.year,
        month: dateStyle.month,
        day: dateStyle.day
    })
    return newFormatter
}
export function ofLocalizedTime(timeStyle: Intl.DateTimeFormatOptions): DateTimeFormatter {
    const newFormatter = DateTimeFormatter.ofPattern("")
    newFormatter.format = altFormat({
        localeMatcher: timeStyle.localeMatcher,
        hour: timeStyle.hour,
        minute: timeStyle.minute,
        second: timeStyle.second,
        timeZoneName: timeStyle.timeZoneName,
        formatMatcher: timeStyle.formatMatcher,
        hour12: timeStyle.hour12,
        timeZone: timeStyle.timeZone,
    })
    return newFormatter
}
export function ofLocalizedDateTime(dateStyle: Intl.DateTimeFormatOptions, timeStyle: Intl.DateTimeFormatOptions = dateStyle): DateTimeFormatter {
    const newFormatter = DateTimeFormatter.ofPattern("")
    newFormatter.format = altFormat({
        localeMatcher: dateStyle.localeMatcher,
        weekday: dateStyle.weekday,
        era: dateStyle.era,
        year: dateStyle.year,
        month: dateStyle.month,
        day: dateStyle.day,
        hour: timeStyle.hour,
        minute: timeStyle.minute,
        second: timeStyle.second,
        timeZoneName: timeStyle.timeZoneName,
        formatMatcher: timeStyle.formatMatcher,
        hour12: timeStyle.hour12,
        timeZone: timeStyle.timeZone,
    })
    return newFormatter
}

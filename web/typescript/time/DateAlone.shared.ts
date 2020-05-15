// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: time/DateAlone.shared.kt
// Package: com.lightningkite.khrysalis.time
// FQImport: com.lightningkite.khrysalis.time.ClockPartSize.None TS None
// FQImport: com.lightningkite.khrysalis.time.DateAlone TS DateAlone
// FQImport: com.lightningkite.khrysalis.time.DateAlone.month TS month
// FQImport: com.lightningkite.khrysalis.time.DateAlone.day TS day
// FQImport: com.lightningkite.khrysalis.time.ClockPartSize TS ClockPartSize
// FQImport: com.lightningkite.khrysalis.time.dateFrom TS dateFrom
// FQImport: com.lightningkite.khrysalis.time.DateAlone.year TS year
// FQImport: com.lightningkite.khrysalis.time.TimeAlone.Companion.noon TS noon
// FQImport: com.lightningkite.khrysalis.time.format TS javaUtilDateFormat
// FQImport: com.lightningkite.khrysalis.time.set.other TS other
// FQImport: com.lightningkite.khrysalis.time.format.clockPartSize TS clockPartSize
import { dateFrom } from './Date.actual'
import { javaUtilDateFormat } from './TimeAlone.shared'
import { DateAlone } from './DateAlone.actual'
import { ClockPartSize } from './ClockPartSize.shared'

//! Declares com.lightningkite.khrysalis.time.set
export function comLightningkiteKhrysalisTimeDateAloneSet(this_Set: DateAlone, other: DateAlone): DateAlone{
    this_Set.year = other.year;
    this_Set.month = other.month;
    this_Set.day = other.day;
    return this_Set;
}

//! Declares com.lightningkite.khrysalis.time.format
export function comLightningkiteKhrysalisTimeDateAloneFormat(this_Format: DateAlone, clockPartSize: ClockPartSize): string{ return javaUtilDateFormat(dateFrom(this_Format, TimeAlone.Companion.INSTANCE.noon, undefined), clockPartSize, ClockPartSize.None); }


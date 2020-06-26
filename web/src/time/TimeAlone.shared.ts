// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: time/TimeAlone.shared.kt
// Package: com.lightningkite.khrysalis.time
import { TimeAlone } from './TimeAlone.actual'
import { kotlinIntFloorDiv, kotlinIntFloorMod } from '../Math.shared'
import { dateFrom, getJavaUtilDateDateAlone, javaUtilDateFormat } from './Date.actual'
import { ClockPartSize } from './ClockPartSize.shared'

//! Declares com.lightningkite.khrysalis.time.normalize>com.lightningkite.khrysalis.time.TimeAlone
export function comLightningkiteKhrysalisTimeTimeAloneNormalize(this_: TimeAlone): void {
    this_.hour = kotlinIntFloorMod((this_.hour + kotlinIntFloorDiv(this_.minute, 60)), 24);
    this_.minute = kotlinIntFloorMod((this_.minute + kotlinIntFloorDiv(this_.second, 60)), 60);
    this_.second = kotlinIntFloorMod(this_.second, 60);
}

//! Declares com.lightningkite.khrysalis.time.set>com.lightningkite.khrysalis.time.TimeAlone
export function comLightningkiteKhrysalisTimeTimeAloneSet(this_: TimeAlone, other: TimeAlone): TimeAlone {
    this_.hour = other.hour;
    this_.minute = other.minute;
    this_.second = other.second;
    return this_;
}


//! Declares com.lightningkite.khrysalis.time.format>com.lightningkite.khrysalis.time.TimeAlone
export function comLightningkiteKhrysalisTimeTimeAloneFormat(this_: TimeAlone, clockPartSize: ClockPartSize): string { return javaUtilDateFormat(dateFrom(getJavaUtilDateDateAlone(new Date()), this_, undefined), ClockPartSize.None, clockPartSize); }


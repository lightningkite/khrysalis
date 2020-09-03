
import {TimeAlone} from './TimeAlone.actual'
import {DateAlone} from './DateAlone.actual'
import {TimeInterval} from './TimeInterval.actual'
import {also} from 'Kotlin'
import {ClockPartSize} from './ClockPartSize.shared'

//! Declares com.lightningkite.khrysalis.time.plus
export function xDatePlus(this_: Date, interval: TimeInterval): Date {
    let newDate = new Date();
    newDate.setTime(this_.getTime() + interval.milliseconds);
    return newDate;
}


//! Declares com.lightningkite.khrysalis.time.minus
export function xDateMinus(this_: Date, interval: TimeInterval): Date {
    let newDate = new Date();
    newDate.setTime(this_.getTime() - interval.milliseconds);
    return newDate;
}

export function copyDateMod(on: Date, action: Function, ...params: Array<any>): Date {
    const to = new Date(on);
    action.call(to, ...params);
    return to;
}

export function dateMod(on: Date, action: Function, ...params: Array<any>): Date {
    action.call(on, ...params);
    return on;
}

export function copyDateModRelative(on: Date, getter: () => number, action: Function, num: number): Date {
    const to = new Date(on);
    action.call(to, getter.call(to) + num);
    return to;
}

export function dateModRelative(on: Date, getter: () => number, action: Function, num: number): Date {
    action.call(on, getter.call(on) + num);
    return on;
}

let tempDate = new Date();
export function copyDateAloneMod(on: DateAlone, action: Function, ...params: Array<any>): DateAlone {
    xDateSet(tempDate, on);
    action.call(tempDate, ...params);
    return xDateDateAloneGet(tempDate);
}

export function dateAloneMod(on: DateAlone, action: Function, ...params: Array<any>): DateAlone {
    xDateSet(tempDate, on);
    action.call(tempDate, ...params);
    xDateAloneSet(on, tempDate);
    return on;
}

export function copyDateAloneModRelative(on: DateAlone, getter: () => number, action: Function, num: number): DateAlone {
    xDateSet(tempDate, on);
    action.call(tempDate, getter.call(tempDate) + num);
    return xDateDateAloneGet(tempDate);
}

export function dateAloneModRelative(on: DateAlone, getter: () => number, action: Function, num: number): DateAlone {
    xDateSet(tempDate, on);
    action.call(tempDate, getter.call(tempDate) + num);
    xDateAloneSet(on, tempDate);
    return on;
}


//! Declares com.lightningkite.khrysalis.time.set
export function xDateSet(this_: Date, ...things: Array<any>): Date {
    for (const item of things) {
        if (item instanceof DateAlone) {
            this_.setFullYear(item.year);
            this_.setMonth(item.month - 1);
            this_.setDate(item.day);
        } else if (item instanceof TimeAlone) {
            this_.setHours(item.hour);
            this_.setMinutes(item.minute);
            this_.setSeconds(item.second);
        } else if (item instanceof Date) {
            this_.setTime(item.getTime())
        }
    }
    return this_;
}

//! Declares com.lightningkite.khrysalis.time.set
export function xDateAloneSet(this_: DateAlone, date: Date): DateAlone {
    this_.year = date.getFullYear()
    this_.month = date.getMonth() + 1
    this_.day = date.getDate()
    return this_;
}

//! Declares com.lightningkite.khrysalis.time.set
export function xTimeAloneSet(this_: TimeAlone, date: Date): TimeAlone {
    this_.hour = date.getHours()
    this_.minute = date.getMinutes()
    this_.second = date.getSeconds()
    return this_;
}

//! Declares com.lightningkite.khrysalis.time.dateAlone
export function xDateDateAloneGet(this_: Date): DateAlone {
    return new DateAlone(
        this_.getFullYear(),
        this_.getMonth() + 1,
        this_.getDate()
    );
}

//! Declares com.lightningkite.khrysalis.time.timeAlone
export function xDateTimeAloneGet(this_: Date): TimeAlone {
    return new TimeAlone(
        this_.getHours(),
        this_.getMinutes(),
        this_.getSeconds()
    );
}

//! Declares com.lightningkite.khrysalis.time.dateFrom
export function dateFrom(dateAlone: DateAlone, timeAlone: TimeAlone, existing: Date = new Date()): Date {
    return xDateSet(existing, dateAlone, timeAlone);
}

//! Declares com.lightningkite.khrysalis.time.dateFromIso
export function dateFromIso(iso8601: string): (Date | null) {
    return new Date(iso8601);
}

//! Declares com.lightningkite.khrysalis.time.format>java.util.Date
export function xDateFormat(this_: Date, dateStyle: ClockPartSize, timeStyle: ClockPartSize): string {

    let dateFormat: Intl.DateTimeFormatOptions = {}

    switch (dateStyle) {
        case ClockPartSize.None:
            break
        case ClockPartSize.Short:
            dateFormat.year = "numeric";
            dateFormat.month = "numeric";
            dateFormat.day = "numeric";
            break
        case ClockPartSize.Medium:
            dateFormat.year = "numeric";
            dateFormat.month = "short";
            dateFormat.day = "numeric";
            break
        case ClockPartSize.Long:
            dateFormat.year = "numeric";
            dateFormat.month = "long";
            dateFormat.day = "numeric";
            break
        case ClockPartSize.Full:
            dateFormat.year = "numeric";
            dateFormat.month = "long";
            dateFormat.day = "numeric";
            dateFormat.weekday = "long";
            dateFormat.era = "short";
            break
    }

    switch (timeStyle) {
        case ClockPartSize.None:
            break
        case ClockPartSize.Short:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            break
        case ClockPartSize.Medium:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            dateFormat.second = "numeric";
            break
        case ClockPartSize.Long:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            dateFormat.second = "numeric";
            break
        case ClockPartSize.Full:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            dateFormat.second = "numeric";
            break
    }

    return this_.toLocaleDateString(undefined, dateFormat);
}
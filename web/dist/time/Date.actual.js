"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const TimeAlone_actual_1 = require("./TimeAlone.actual");
const DateAlone_actual_1 = require("./DateAlone.actual");
const ClockPartSize_shared_1 = require("./ClockPartSize.shared");
//! Declares com.lightningkite.khrysalis.time.plus
function xDatePlus(this_, interval) {
    let newDate = new Date();
    newDate.setTime(this_.getTime() + interval.milliseconds);
    return newDate;
}
exports.xDatePlus = xDatePlus;
//! Declares com.lightningkite.khrysalis.time.minus
function xDateMinus(this_, interval) {
    let newDate = new Date();
    newDate.setTime(this_.getTime() - interval.milliseconds);
    return newDate;
}
exports.xDateMinus = xDateMinus;
function copyDateMod(on, action, ...params) {
    const to = new Date(on);
    action.call(to, ...params);
    return to;
}
exports.copyDateMod = copyDateMod;
function dateMod(on, action, ...params) {
    action.call(on, ...params);
    return on;
}
exports.dateMod = dateMod;
function copyDateModRelative(on, getter, action, num) {
    const to = new Date(on);
    action.call(to, getter.call(to) + num);
    return to;
}
exports.copyDateModRelative = copyDateModRelative;
function dateModRelative(on, getter, action, num) {
    action.call(on, getter.call(on) + num);
    return on;
}
exports.dateModRelative = dateModRelative;
let tempDate = new Date();
function copyDateAloneMod(on, action, ...params) {
    xDateSet(tempDate, on);
    action.call(tempDate, ...params);
    return xDateDateAloneGet(tempDate);
}
exports.copyDateAloneMod = copyDateAloneMod;
function dateAloneMod(on, action, ...params) {
    xDateSet(tempDate, on);
    action.call(tempDate, ...params);
    xDateAloneSet(on, tempDate);
    return on;
}
exports.dateAloneMod = dateAloneMod;
function copyDateAloneModRelative(on, getter, action, num) {
    xDateSet(tempDate, on);
    action.call(tempDate, getter.call(tempDate) + num);
    return xDateDateAloneGet(tempDate);
}
exports.copyDateAloneModRelative = copyDateAloneModRelative;
function dateAloneModRelative(on, getter, action, num) {
    xDateSet(tempDate, on);
    action.call(tempDate, getter.call(tempDate) + num);
    xDateAloneSet(on, tempDate);
    return on;
}
exports.dateAloneModRelative = dateAloneModRelative;
//! Declares com.lightningkite.khrysalis.time.set
function xDateSet(this_, ...things) {
    for (const item of things) {
        if (item instanceof DateAlone_actual_1.DateAlone) {
            this_.setFullYear(item.year);
            this_.setMonth(item.month - 1);
            this_.setDate(item.day);
        }
        else if (item instanceof TimeAlone_actual_1.TimeAlone) {
            this_.setHours(item.hour);
            this_.setMinutes(item.minute);
            this_.setSeconds(item.second);
        }
        else if (item instanceof Date) {
            this_.setTime(item.getTime());
        }
    }
    return this_;
}
exports.xDateSet = xDateSet;
//! Declares com.lightningkite.khrysalis.time.set
function xDateAloneSet(this_, date) {
    this_.year = date.getFullYear();
    this_.month = date.getMonth() + 1;
    this_.day = date.getDate();
    return this_;
}
exports.xDateAloneSet = xDateAloneSet;
//! Declares com.lightningkite.khrysalis.time.set
function xTimeAloneSet(this_, date) {
    this_.hour = date.getHours();
    this_.minute = date.getMinutes();
    this_.second = date.getSeconds();
    return this_;
}
exports.xTimeAloneSet = xTimeAloneSet;
//! Declares com.lightningkite.khrysalis.time.dateAlone
function xDateDateAloneGet(this_) {
    return new DateAlone_actual_1.DateAlone(this_.getFullYear(), this_.getMonth() + 1, this_.getDate());
}
exports.xDateDateAloneGet = xDateDateAloneGet;
//! Declares com.lightningkite.khrysalis.time.timeAlone
function xDateTimeAloneGet(this_) {
    return new TimeAlone_actual_1.TimeAlone(this_.getHours(), this_.getMinutes(), this_.getSeconds());
}
exports.xDateTimeAloneGet = xDateTimeAloneGet;
//! Declares com.lightningkite.khrysalis.time.dateFrom
function dateFrom(dateAlone, timeAlone, existing = new Date()) {
    return xDateSet(existing, dateAlone, timeAlone);
}
exports.dateFrom = dateFrom;
//! Declares com.lightningkite.khrysalis.time.dateFromIso
function dateFromIso(iso8601) {
    return new Date(iso8601);
}
exports.dateFromIso = dateFromIso;
//! Declares com.lightningkite.khrysalis.time.format>java.util.Date
function xDateFormat(this_, dateStyle, timeStyle) {
    let dateFormat = {};
    switch (dateStyle) {
        case ClockPartSize_shared_1.ClockPartSize.None:
            break;
        case ClockPartSize_shared_1.ClockPartSize.Short:
            dateFormat.year = "numeric";
            dateFormat.month = "numeric";
            dateFormat.day = "numeric";
            break;
        case ClockPartSize_shared_1.ClockPartSize.Medium:
            dateFormat.year = "numeric";
            dateFormat.month = "short";
            dateFormat.day = "numeric";
            break;
        case ClockPartSize_shared_1.ClockPartSize.Long:
            dateFormat.year = "numeric";
            dateFormat.month = "long";
            dateFormat.day = "numeric";
            break;
        case ClockPartSize_shared_1.ClockPartSize.Full:
            dateFormat.year = "numeric";
            dateFormat.month = "long";
            dateFormat.day = "numeric";
            dateFormat.weekday = "long";
            dateFormat.era = "short";
            break;
    }
    switch (timeStyle) {
        case ClockPartSize_shared_1.ClockPartSize.None:
            break;
        case ClockPartSize_shared_1.ClockPartSize.Short:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            break;
        case ClockPartSize_shared_1.ClockPartSize.Medium:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            dateFormat.second = "numeric";
            break;
        case ClockPartSize_shared_1.ClockPartSize.Long:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            dateFormat.second = "numeric";
            break;
        case ClockPartSize_shared_1.ClockPartSize.Full:
            dateFormat.hour = "numeric";
            dateFormat.minute = "numeric";
            dateFormat.second = "numeric";
            break;
    }
    return this_.toLocaleDateString(undefined, dateFormat);
}
exports.xDateFormat = xDateFormat;
//# sourceMappingURL=Date.actual.js.map
"use strict";
var _a;
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: time/TimeAlone.actual.kt
// Package: com.lightningkite.khrysalis.time
const TimeInterval_actual_1 = require("./TimeInterval.actual");
const Date_actual_1 = require("./Date.actual");
const Kotlin_1 = require("../Kotlin");
//! Declares com.lightningkite.khrysalis.time.TimeAlone
class TimeAlone {
    constructor(hour, minute, second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    hashCode() {
        let hash = 17;
        hash = 31 * hash + this.hour;
        hash = 31 * hash + this.minute;
        hash = 31 * hash + this.second;
        return hash;
    }
    equals(other) { return other instanceof TimeAlone && this.hour === other.hour && this.minute === other.minute && this.second === other.second; }
    toString() { return `TimeAlone(hour = ${this.hour}, minute = ${this.minute}, second = ${this.second})`; }
    copy(hour = this.hour, minute = this.minute, second = this.second) { return new TimeAlone(hour, minute, second); }
    toJSON() {
        return comLightningkiteKhrysalisTimeTimeAloneIso8601(this);
    }
    //! Declares com.lightningkite.khrysalis.time.TimeAlone.comparable
    get comparable() { return this.hour * 60 * 60 + this.minute * 60 + this.second; }
    //! Declares com.lightningkite.khrysalis.time.TimeAlone.secondsInDay
    get secondsInDay() { return this.hour * 60 * 60 + this.minute * 60 + this.second; }
    set secondsInDay(value) {
        this.hour = value / 60 / 60;
        this.minute = value / 60 % 60;
        this.second = value % 60;
    }
    //! Declares com.lightningkite.khrysalis.time.TimeAlone.hoursInDay
    get hoursInDay() { return this.hour + this.minute / 60 + this.second / 3600 + 0.5 / 3600; }
    set hoursInDay(value) {
        this.hour = Math.floor(value);
        this.minute = Math.floor((value * 60)) % 60;
        this.second = Math.floor((value * 3600)) % 60;
    }
}
exports.TimeAlone = TimeAlone;
TimeAlone.Companion = (_a = class Companion {
        constructor() {
            this.min = new TimeAlone(0, 0, 0);
            this.midnight = this.min;
            this.noon = new TimeAlone(12, 0, 0);
            this.max = new TimeAlone(23, 59, 59);
            this.min = new TimeAlone(0, 0, 0);
            this.midnight = this.min;
            this.noon = new TimeAlone(12, 0, 0);
            this.max = new TimeAlone(23, 59, 59);
        }
        now() { return Date_actual_1.getJavaUtilDateTimeAlone(new Date()); }
        iso(string) {
            const parts = string.split(':');
            const hour = Kotlin_1.parseIntOrNull(parts[0]);
            if (hour === null)
                return null;
            const minute = Kotlin_1.parseIntOrNull(parts[1]);
            if (minute === null)
                return null;
            let second = Kotlin_1.parseIntOrNull(parts[2]);
            if (second === null)
                second = 0;
            return new TimeAlone(hour, minute, second);
        }
    },
    _a.INSTANCE = new _a(),
    _a);
//! Declares com.lightningkite.khrysalis.time.iso8601
function comLightningkiteKhrysalisTimeTimeAloneIso8601(this_) {
    return `${this_.hour.toString().padStart(2, "0")}:${this_.minute.toString().padStart(2, "0")}:${this_.second.toString().padStart(2, "0")}`;
}
exports.comLightningkiteKhrysalisTimeTimeAloneIso8601 = comLightningkiteKhrysalisTimeTimeAloneIso8601;
//! Declares com.lightningkite.khrysalis.time.minus
function comLightningkiteKhrysalisTimeTimeAloneMinus(this_, rhs) {
    let offset = 0;
    if (rhs instanceof TimeAlone) {
        offset = (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second);
    }
    else if (rhs instanceof TimeInterval_actual_1.TimeInterval) {
        offset = rhs.milliseconds / 1000;
    }
    const result = (this_.hour * 60 * 60 + this_.minute * 60 + this_.second) - offset;
    return (() => {
        if (result < 0) {
            return new TimeAlone(0, 0, 0);
        }
        else {
            return new TimeAlone(Math.floor(result / 60 / 60), Math.floor(result / 60 % 60), Math.floor(result % 60));
        }
    })();
}
exports.comLightningkiteKhrysalisTimeTimeAloneMinus = comLightningkiteKhrysalisTimeTimeAloneMinus;
//! Declares com.lightningkite.khrysalis.time.plus
function comLightningkiteKhrysalisTimeTimeAlonePlus(this_, rhs) {
    let offset = 0;
    if (rhs instanceof TimeAlone) {
        offset = (rhs.hour * 60 * 60 + rhs.minute * 60 + rhs.second);
    }
    else if (rhs instanceof TimeInterval_actual_1.TimeInterval) {
        offset = rhs.milliseconds / 1000;
    }
    const result = (this_.hour * 60 * 60 + this_.minute * 60 + this_.second) + offset;
    return new TimeAlone(Math.floor(result / 60 / 60), Math.floor(result / 60 % 60), Math.floor(result % 60));
}
exports.comLightningkiteKhrysalisTimeTimeAlonePlus = comLightningkiteKhrysalisTimeTimeAlonePlus;
//# sourceMappingURL=TimeAlone.actual.js.map
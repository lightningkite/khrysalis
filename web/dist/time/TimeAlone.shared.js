"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Math_shared_1 = require("../Math.shared");
const Date_actual_1 = require("./Date.actual");
const ClockPartSize_shared_1 = require("./ClockPartSize.shared");
//! Declares com.lightningkite.khrysalis.time.normalize>com.lightningkite.khrysalis.time.TimeAlone
function comLightningkiteKhrysalisTimeTimeAloneNormalize(this_) {
    this_.hour = Math_shared_1.kotlinIntFloorMod((this_.hour + Math_shared_1.kotlinIntFloorDiv(this_.minute, 60)), 24);
    this_.minute = Math_shared_1.kotlinIntFloorMod((this_.minute + Math_shared_1.kotlinIntFloorDiv(this_.second, 60)), 60);
    this_.second = Math_shared_1.kotlinIntFloorMod(this_.second, 60);
}
exports.comLightningkiteKhrysalisTimeTimeAloneNormalize = comLightningkiteKhrysalisTimeTimeAloneNormalize;
//! Declares com.lightningkite.khrysalis.time.set>com.lightningkite.khrysalis.time.TimeAlone
function comLightningkiteKhrysalisTimeTimeAloneSet(this_, other) {
    this_.hour = other.hour;
    this_.minute = other.minute;
    this_.second = other.second;
    return this_;
}
exports.comLightningkiteKhrysalisTimeTimeAloneSet = comLightningkiteKhrysalisTimeTimeAloneSet;
//! Declares com.lightningkite.khrysalis.time.format>com.lightningkite.khrysalis.time.TimeAlone
function comLightningkiteKhrysalisTimeTimeAloneFormat(this_, clockPartSize) { return Date_actual_1.javaUtilDateFormat(Date_actual_1.dateFrom(Date_actual_1.getJavaUtilDateDateAlone(new Date()), this_, undefined), ClockPartSize_shared_1.ClockPartSize.None, clockPartSize); }
exports.comLightningkiteKhrysalisTimeTimeAloneFormat = comLightningkiteKhrysalisTimeTimeAloneFormat;
//# sourceMappingURL=TimeAlone.shared.js.map
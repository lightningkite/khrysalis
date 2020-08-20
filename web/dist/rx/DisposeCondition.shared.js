"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: rx/DisposeCondition.shared.kt
// Package: com.lightningkite.khrysalis.rx
const DisposeCondition_actual_1 = require("./DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.rx.DisposeCondition
class DisposeCondition {
    constructor(call) {
        this.call = call;
    }
}
exports.DisposeCondition = DisposeCondition;
//! Declares com.lightningkite.khrysalis.rx.and>com.lightningkite.khrysalis.rx.DisposeCondition
function comLightningkiteKhrysalisRxDisposeConditionAnd(this_, other) {
    return andAllDisposeConditions([this_, other]);
}
exports.comLightningkiteKhrysalisRxDisposeConditionAnd = comLightningkiteKhrysalisRxDisposeConditionAnd;
//! Declares com.lightningkite.khrysalis.rx.andAllDisposeConditions
function andAllDisposeConditions(list) {
    return new DisposeCondition((it) => {
        let disposalsLeft = list.length;
        for (const item of list) {
            item.call(new DisposeCondition_actual_1.DisposableLambda(() => {
                disposalsLeft = disposalsLeft - 1;
                if (disposalsLeft === 0) {
                    it.unsubscribe();
                }
            }));
        }
    });
}
exports.andAllDisposeConditions = andAllDisposeConditions;
//! Declares com.lightningkite.khrysalis.rx.or>com.lightningkite.khrysalis.rx.DisposeCondition
function comLightningkiteKhrysalisRxDisposeConditionOr(this_, other) {
    return new DisposeCondition((it) => {
        this_.call(it);
        other.call(it);
    });
}
exports.comLightningkiteKhrysalisRxDisposeConditionOr = comLightningkiteKhrysalisRxDisposeConditionOr;
//# sourceMappingURL=DisposeCondition.shared.js.map
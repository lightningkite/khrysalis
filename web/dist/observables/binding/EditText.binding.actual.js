"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: observables/binding/EditText.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
const ObservableProperty_ext_shared_1 = require("../ObservableProperty.ext.shared");
const Kotlin_1 = require("Kotlin");
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.observables.binding.bindString>android.widget.EditText
function androidWidgetEditTextBindString(this_, observable) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
        if (!(observable.value === this_.value.toString())) {
            this_.value = observable.value;
        }
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        const s = this_.value;
        if (!(observable.value === s)) {
            observable.value = s;
        }
    };
}
exports.androidWidgetEditTextBindString = androidWidgetEditTextBindString;
//! Declares com.lightningkite.khrysalis.observables.binding.bindInteger>android.widget.EditText
function androidWidgetEditTextBindInteger(this_, observable) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
        var _a, _b;
        const currentValue = Kotlin_1.parseIntOrNull(undefined);
        if (!(value === currentValue)) {
            this_.value = (_b = (_a = Kotlin_1.takeUnless(value, (it) => it === 0)) === null || _a === void 0 ? void 0 : _a.toString()) !== null && _b !== void 0 ? _b : "";
        }
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        var _a;
        const currentValue = (_a = Kotlin_1.parseIntOrNull(this_.value)) !== null && _a !== void 0 ? _a : 0;
        if (observable.value !== currentValue) {
            observable.value = currentValue;
        }
    };
}
exports.androidWidgetEditTextBindInteger = androidWidgetEditTextBindInteger;
//! Declares com.lightningkite.khrysalis.observables.binding.bindDouble>android.widget.EditText
function androidWidgetEditTextBindDouble(this_, observable) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
        var _a, _b;
        const currentValue = Kotlin_1.parseFloatOrNull(undefined);
        if (!(value === currentValue)) {
            this_.value = (_b = (_a = Kotlin_1.takeUnless(value, (it) => it === 0.0)) === null || _a === void 0 ? void 0 : _a.toString()) !== null && _b !== void 0 ? _b : "";
        }
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        var _a;
        const currentValue = (_a = Kotlin_1.parseFloatOrNull(this_.value)) !== null && _a !== void 0 ? _a : 0;
        if (observable.value !== currentValue) {
            observable.value = currentValue;
        }
    };
}
exports.androidWidgetEditTextBindDouble = androidWidgetEditTextBindDouble;

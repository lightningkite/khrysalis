"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: observables/binding/DateButton.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
const ObservableProperty_ext_shared_1 = require("../ObservableProperty.ext.shared");
const Date_actual_1 = require("../../time/Date.actual");
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.observables.binding.bind>com.lightningkite.khrysalis.views.android.DateButton
function comLightningkiteKhrysalisViewsAndroidDateButtonBind(this_, date) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(date, undefined, undefined, (it) => {
        this_.valueAsDate = it;
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        date.value = this_.valueAsDate;
    };
}
exports.comLightningkiteKhrysalisViewsAndroidDateButtonBind = comLightningkiteKhrysalisViewsAndroidDateButtonBind;
//! Declares com.lightningkite.khrysalis.observables.binding.bind>com.lightningkite.khrysalis.views.android.TimeButton
function comLightningkiteKhrysalisViewsAndroidTimeButtonBind(this_, date, minuteInterval = 1) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(date, undefined, undefined, (it) => {
        this_.valueAsDate = it;
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        date.value = this_.valueAsDate;
    };
}
exports.comLightningkiteKhrysalisViewsAndroidTimeButtonBind = comLightningkiteKhrysalisViewsAndroidTimeButtonBind;
//! Declares com.lightningkite.khrysalis.observables.binding.bindDateAlone>com.lightningkite.khrysalis.views.android.DateButton
function comLightningkiteKhrysalisViewsAndroidDateButtonBindDateAlone(this_, date) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(date, undefined, undefined, (it) => {
        this_.valueAsDate = Date_actual_1.dateFrom(it, Date_actual_1.getJavaUtilDateTimeAlone(Date.constructor()), undefined);
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        date.value = Date_actual_1.getJavaUtilDateDateAlone(this_.valueAsDate);
    };
}
exports.comLightningkiteKhrysalisViewsAndroidDateButtonBindDateAlone = comLightningkiteKhrysalisViewsAndroidDateButtonBindDateAlone;
//! Declares com.lightningkite.khrysalis.observables.binding.bindTimeAlone>com.lightningkite.khrysalis.views.android.TimeButton
function comLightningkiteKhrysalisViewsAndroidTimeButtonBindTimeAlone(this_, date, minuteInterval = 1) {
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(date, undefined, undefined, (it) => {
        this_.valueAsDate = Date_actual_1.dateFrom(Date_actual_1.getJavaUtilDateDateAlone(Date.constructor()), it, undefined);
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        date.value = Date_actual_1.getJavaUtilDateTimeAlone(this_.valueAsDate);
    };
}
exports.comLightningkiteKhrysalisViewsAndroidTimeButtonBindTimeAlone = comLightningkiteKhrysalisViewsAndroidTimeButtonBindTimeAlone;

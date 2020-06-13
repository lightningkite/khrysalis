"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: observables/binding/SeekBar.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
const ObservableProperty_ext_shared_1 = require("../ObservableProperty.ext.shared");
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.SeekBar
function androidWidgetSeekBarBind(this_, start, endInclusive, observable) {
    this_.min = start.toString();
    this_.max = endInclusive.toString();
    let suppress = false;
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
        if (!suppress) {
            suppress = true;
            this_.valueAsNumber = (value);
            suppress = false;
        }
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        if (!suppress) {
            suppress = true;
            observable.value = start;
            suppress = false;
        }
    };
}
exports.androidWidgetSeekBarBind = androidWidgetSeekBarBind;

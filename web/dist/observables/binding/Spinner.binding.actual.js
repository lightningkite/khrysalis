"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: observables/binding/Spinner.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
const ObservableProperty_ext_shared_1 = require("../ObservableProperty.ext.shared");
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
const StandardObservableProperty_shared_1 = require("../StandardObservableProperty.shared");
const viewAttached_1 = require("../../views/viewAttached");
//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.Spinner
function spinnerBindAdvanced(this_, options, selected, makeView) {
    const observables = options.value.map((x) => {
        return new StandardObservableProperty_shared_1.StandardObservableProperty(x);
    });
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(options, undefined, undefined, (options) => {
        //correct number of options
        const diff = options.length - this_.options.length;
        if (diff > 0) {
            for (let i = 0; i < diff; i++) {
                const newOpt = document.createElement("option");
                newOpt.value = (options.length - 1 - diff + i).toString();
                const newObs = new StandardObservableProperty_shared_1.StandardObservableProperty(options[options.length - diff + i]);
                makeView(newObs);
                this_.options.add(newOpt);
                observables.push(newObs);
            }
        }
        else if (diff < 0) {
            for (let i = 0; i < -diff; i++) {
                const opt = this_.options.item(this_.options.length - 1);
                viewAttached_1.triggerDetatchEvent(opt);
                this_.options.remove(this_.options.length - 1);
                observables.pop();
            }
        }
        for (let i = 0; i < options.length; i++) {
            observables[i].value = options[i];
        }
        this_.selectedIndex = options.indexOf(selected.value);
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(selected, undefined, undefined, (sel) => {
        this_.selectedIndex = options.value.indexOf(sel);
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.oninput = (ev) => {
        const sel = options.value[this_.selectedIndex];
        if (sel !== undefined) {
            selected.value = sel;
        }
    };
}
exports.spinnerBindAdvanced = spinnerBindAdvanced;
//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.Spinner
function spinnerBind(this_, options, selected, toString = (x) => `${x}`) {
    const observables = options.value.map((x) => {
        return new StandardObservableProperty_shared_1.StandardObservableProperty(x);
    });
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(options, undefined, undefined, (options) => {
        //correct number of options
        const diff = options.length - this_.options.length;
        if (diff > 0) {
            for (let i = 0; i < diff; i++) {
                const newOpt = document.createElement("option");
                newOpt.value = (options.length - 1 - diff + i).toString();
                const newObs = new StandardObservableProperty_shared_1.StandardObservableProperty(options[options.length - diff + i]);
                DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(newObs, undefined, undefined, (x) => {
                    newOpt.innerText = toString(x);
                }), DisposeCondition_actual_1.getAndroidViewViewRemoved(newOpt));
                this_.options.add(newOpt);
                observables.push(newObs);
            }
        }
        else if (diff < 0) {
            for (let i = 0; i < -diff; i++) {
                const opt = this_.options.item(this_.options.length - 1);
                viewAttached_1.triggerDetatchEvent(opt);
                this_.options.remove(this_.options.length - 1);
                observables.pop();
            }
        }
        for (let i = 0; i < options.length; i++) {
            observables[i].value = options[i];
        }
        this_.selectedIndex = options.indexOf(selected.value);
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(selected, undefined, undefined, (sel) => {
        this_.selectedIndex = options.value.indexOf(sel);
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    this_.oninput = (ev) => {
        const sel = options.value[this_.selectedIndex];
        if (sel !== undefined) {
            selected.value = sel;
        }
    };
}
exports.spinnerBind = spinnerBind;
//# sourceMappingURL=Spinner.binding.actual.js.map
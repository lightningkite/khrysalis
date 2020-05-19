// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/CompoundButton.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<anonymous>.isChecked TS isChecked
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelect.<anonymous>.isChecked TS isChecked
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.T TS T
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.suppress TS suppress
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelect.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelect.value TS value
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.value TS value
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelect.<anonymous>.shouldBeChecked TS shouldBeChecked
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectNullable.observable TS observable
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectNullable.<anonymous>.shouldBeChecked TS shouldBeChecked
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.<anonymous>.isChecked TS isChecked
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.<anonymous>.shouldBeChecked TS shouldBeChecked
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.observable TS observable
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy TS comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectNullable.value TS value
// FQImport: isChecked TS setAndroidWidgetCompoundButtonIsChecked
// FQImport: android.widget.CompoundButton.setOnCheckedChangeListener TS setOnCheckedChangeListener
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectNullable.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.observable TS observable
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.rx.until TS ioReactivexDisposablesDisposableUntil
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelect.T TS T
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectNullable.<anonymous>.isChecked TS isChecked
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectInvert.<anonymous>.buttonView TS buttonView
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelect.observable TS observable
// FQImport: android.widget.CompoundButton TS CompoundButton
// FQImport: com.lightningkite.khrysalis.rx.removed TS getAndroidViewViewRemoved
// FQImport: com.lightningkite.khrysalis.observables.binding.bindSelectNullable.T TS T
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty TS MutableObservableProperty
// FQImport: isChecked TS getAndroidWidgetCompoundButtonIsChecked
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from './../ObservableProperty.ext.shared'
import { MutableObservableProperty } from './../MutableObservableProperty.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from './../../rx/DisposeCondition.actual'

//! Declares com.lightningkite.khrysalis.observables.binding.bindSelect
export function androidWidgetCompoundButtonBindSelect<T>(this_: CompoundButton, value: T, observable: MutableObservableProperty<T>): void{
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (it) => {
                const shouldBeChecked = it.equals(value);
                
                if (!(getAndroidWidgetCompoundButtonIsChecked(this_) === shouldBeChecked)) {
                    setAndroidWidgetCompoundButtonIsChecked(this_, shouldBeChecked);
                }
    }), getAndroidViewViewRemoved(this_));
    this_.setOnCheckedChangeListener((buttonView, isChecked) => {
            if (isChecked && !(observable.value.equals(value))) {
                observable.value = value;
            } else if (isChecked.not() && observable.value.equals(value)) {
                setAndroidWidgetCompoundButtonIsChecked(this_, true);
            }
    });
}


//! Declares com.lightningkite.khrysalis.observables.binding.bindSelectNullable
export function androidWidgetCompoundButtonBindSelectNullable<T>(this_: CompoundButton, value: T, observable: MutableObservableProperty<(T | null)>): void{
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (it) => {
                const shouldBeChecked = it.equals(value);
                
                if (!(getAndroidWidgetCompoundButtonIsChecked(this_) === shouldBeChecked)) {
                    setAndroidWidgetCompoundButtonIsChecked(this_, shouldBeChecked);
                }
    }), getAndroidViewViewRemoved(this_));
    this_.setOnCheckedChangeListener((buttonView, isChecked) => {
            if (isChecked && !(observable.value.equals(value))) {
                observable.value = value;
            } else if (isChecked.not() && observable.value.equals(value)) {
                observable.value = null;
            }
    });
}




//! Declares com.lightningkite.khrysalis.observables.binding.bindSelectInvert
export function androidWidgetCompoundButtonBindSelectInvert<T>(this_: CompoundButton, value: T, observable: MutableObservableProperty<(T | null)>): void{
    let suppress = false;
    
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (it) => {
                if (suppress.not()) {
                    suppress = true;
                    const shouldBeChecked = it.equals(value) || it.equals(null);
                    
                    if (!(getAndroidWidgetCompoundButtonIsChecked(this_) === shouldBeChecked)) {
                        setAndroidWidgetCompoundButtonIsChecked(this_, shouldBeChecked);
                    }
                    suppress = false;
                }
    }), getAndroidViewViewRemoved(this_));
    this_.setOnCheckedChangeListener((buttonView, isChecked) => {
            if (suppress.not()) {
                suppress = true;
                if (isChecked.not() && observable.value.equals(value)) {
                    observable.value = null;
                    setAndroidWidgetCompoundButtonIsChecked(buttonView, true);
                } else if (!(observable.value.equals(value))) {
                    observable.value = value;
                    setAndroidWidgetCompoundButtonIsChecked(buttonView, true);
                }
                suppress = false;
            }
    });
}

//! Declares com.lightningkite.khrysalis.observables.binding.bind
export function androidWidgetCompoundButtonBind(this_: CompoundButton, observable: MutableObservableProperty<Boolean>): void{
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (it) => {
                if (!(it === getAndroidWidgetCompoundButtonIsChecked(this_))) {
                    setAndroidWidgetCompoundButtonIsChecked(this_, it);
                }
    }), getAndroidViewViewRemoved(this_));
    this_.setOnCheckedChangeListener((buttonView, isChecked) => {
            if (!(observable.value === isChecked)) {
                observable.value = isChecked;
            }
    });
}



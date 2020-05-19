// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/View.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
// FQImport: android.view.View TS View
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.rx.until TS ioReactivexDisposablesDisposableUntil
// FQImport: com.lightningkite.khrysalis.observables.binding.bindExists.observable TS observable
// FQImport: com.lightningkite.khrysalis.observables.binding.bindExists.<anonymous>.value TS value
// FQImport: visibility TS setAndroidViewViewVisibility
// FQImport: android.view.View.VISIBLE TS VISIBLE
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty TS ObservableProperty
// FQImport: android.view.View.GONE TS GONE
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy TS comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy
// FQImport: android.view.View.INVISIBLE TS INVISIBLE
// FQImport: com.lightningkite.khrysalis.observables.binding.bindVisible.<anonymous>.value TS value
// FQImport: com.lightningkite.khrysalis.rx.removed TS getAndroidViewViewRemoved
// FQImport: com.lightningkite.khrysalis.observables.binding.bindVisible.observable TS observable
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from './../ObservableProperty.ext.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from './../../rx/DisposeCondition.actual'
import { ObservableProperty } from './../ObservableProperty.shared'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.observables.binding.bindVisible
export function androidViewViewBindVisible(this_: View, observable: ObservableProperty<Boolean>): SubscriptionLike{
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
                setAndroidViewViewVisibility(this_, value ? View.VISIBLE : View.INVISIBLE)
    }), getAndroidViewViewRemoved(this_));
}


//! Declares com.lightningkite.khrysalis.observables.binding.bindExists
export function androidViewViewBindExists(this_: View, observable: ObservableProperty<Boolean>): SubscriptionLike{
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
                setAndroidViewViewVisibility(this_, value ? View.VISIBLE : View.GONE)
    }), getAndroidViewViewRemoved(this_));
}


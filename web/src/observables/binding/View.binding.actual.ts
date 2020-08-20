// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/View.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from '../ObservableProperty.ext.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from '../../rx/DisposeCondition.actual'
import { ObservableProperty } from '../ObservableProperty.shared'
import { setViewVisibility } from '../../views/View.ext.actual'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.observables.binding.bindVisible>android.view.View
export function androidViewViewBindVisible(this_: HTMLElement, observable: ObservableProperty<boolean>): void {
    ioReactivexDisposablesDisposableUntil<SubscriptionLike>(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy<boolean>(observable, undefined, undefined, (value: boolean): void => {
                setViewVisibility(this_, value ? "visible" : "invisible");
    }), getAndroidViewViewRemoved(this_));
}


//! Declares com.lightningkite.khrysalis.observables.binding.bindExists>android.view.View
export function androidViewViewBindExists(this_: HTMLElement, observable: ObservableProperty<boolean>): void {
    ioReactivexDisposablesDisposableUntil<SubscriptionLike>(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy<boolean>(observable, undefined, undefined, (value: boolean): void => {
                setViewVisibility(this_, value ? "visible" : "gone");
    }), getAndroidViewViewRemoved(this_));
}


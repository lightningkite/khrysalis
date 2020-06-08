// Generated by Khrysalis TypeScript converter
// File: observables/binding/Spinner.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from '../ObservableProperty.ext.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from '../../rx/DisposeCondition.actual'
import { ObservableProperty } from '../ObservableProperty.shared'
import { StandardObservableProperty } from '../StandardObservableProperty.shared'
import { IllegalStateException, tryCastClass } from 'Kotlin'
import { MutableObservableProperty } from '../MutableObservableProperty.shared'

//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.Spinner
export function androidWidgetSpinnerBind<T>(this_: HTMLInputElement, options: ObservableProperty<Array<T>>, selected: MutableObservableProperty<T>, makeView: (a: ObservableProperty<T>) => HTMLElement): void {
}


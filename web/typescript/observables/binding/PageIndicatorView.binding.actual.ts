// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/PageIndicatorView.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
// FQImport: com.rd.PageIndicatorView.selection TS setComRdPageIndicatorViewSelection
// FQImport: com.rd.PageIndicatorView.count TS setComRdPageIndicatorViewCount
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.selected TS selected
// FQImport: com.lightningkite.khrysalis.rx.removed>android.view.View TS getAndroidViewViewRemoved
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<anonymous>.value TS value
// FQImport: com.rd.PageIndicatorView TS PageIndicatorView
// FQImport: com.lightningkite.khrysalis.rx.until>io.reactivex.disposables.Disposable TS ioReactivexDisposablesDisposableUntil
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any> TS comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty TS MutableObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.count TS count
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from './../ObservableProperty.ext.shared'
import { MutableObservableProperty } from './../MutableObservableProperty.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from './../../rx/DisposeCondition.actual'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.observables.binding.bind>com.rd.PageIndicatorView
export function comRdPageIndicatorViewBind(this_: PageIndicatorView, count: number = 0, selected: MutableObservableProperty<number>): SubscriptionLike{
    setComRdPageIndicatorViewCount(this_, count);
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(selected, undefined, undefined, (value) => {
                setComRdPageIndicatorViewSelection(this_, value)
    }), getAndroidViewViewRemoved(this_));
}


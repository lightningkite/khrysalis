// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/ObservableProperty.ext.shared.kt
// Package: com.lightningkite.khrysalis.observables
// FQImport: com.lightningkite.khrysalis.observables.includes.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.onNext TS onNext
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.T TS T
// FQImport: com.lightningkite.khrysalis.observables.includes.collection TS collection
// FQImport: kotlin.Throwable.printStackTrace TS printStackTrace
// FQImport: kotlin.collections.Set TS Set
// FQImport: com.lightningkite.khrysalis.observables.observableNN.T TS T
// FQImport: com.lightningkite.khrysalis.observables.observable>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any> TS getComLightningkiteKhrysalisObservablesObservablePropertyObservable
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.onComplete TS onComplete
// FQImport: com.lightningkite.khrysalis.observables.observable.T TS T
// FQImport: com.lightningkite.khrysalis.observables.map>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any> TS comLightningkiteKhrysalisObservablesObservablePropertyMap
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.onChange TS onChange
// FQImport: com.lightningkite.khrysalis.observables.withWrite>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any> TS comLightningkiteKhrysalisObservablesObservablePropertyWithWrite
// FQImport: com.lightningkite.khrysalis.observables.<get-observableNN>.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty TS ObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.onError TS onError
// FQImport: com.lightningkite.khrysalis.observables.includes.E TS E
// FQImport: com.lightningkite.khrysalis.observables.onChangeNN.T TS T
// FQImport: com.lightningkite.khrysalis.observables.includes.element TS element
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.<anonymous>.boxed TS boxed
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.<get-onChangeNN>.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty TS MutableObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.value TS value
import { comLightningkiteKhrysalisObservablesObservablePropertyWithWrite } from './WriteAddedObservableProperty.shared'
import { ObservableProperty } from './ObservableProperty.shared'
import { comLightningkiteKhrysalisObservablesObservablePropertyMap } from './TransformedObservableProperty.shared'
import { Observable, SubscriptionLike, concat as rxConcat, of as rxOf } from 'rxjs'
import { map as rxMap } from 'rxjs/operators'
import { MutableObservableProperty } from './MutableObservableProperty.shared'

//! Declares com.lightningkite.khrysalis.observables.observable>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
export function getComLightningkiteKhrysalisObservablesObservablePropertyObservable<T>(this_: ObservableProperty<T>): Observable<T> { return rxConcat(rxOf(this_.value), this_.onChange); }

//! Declares com.lightningkite.khrysalis.observables.observableNN>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
export function getComLightningkiteKhrysalisObservablesObservablePropertyObservableNN<T>(this_: ObservableProperty<T>): Observable<T> { return rxConcat(rxOf(this_.value), this_.onChange).pipe(rxMap((it) => it)); }

//! Declares com.lightningkite.khrysalis.observables.onChangeNN>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
export function getComLightningkiteKhrysalisObservablesObservablePropertyOnChangeNN<T>(this_: ObservableProperty<T>): Observable<T> { return this_.onChange.pipe(rxMap((it) => it)); }


//! Declares com.lightningkite.khrysalis.observables.subscribeBy>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
export function comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy<T>(this_: ObservableProperty<T>, onError:  (a: any) => void = (it) => {
        it.printStackTrace()
}, onComplete:  () => void = () => {}, onNext:  (a: T) => void = (it) => {}): SubscriptionLike { return getComLightningkiteKhrysalisObservablesObservablePropertyObservable(this_).subscribe((boxed) => {
            onNext(boxed)
}, onError, onComplete); }

//! Declares com.lightningkite.khrysalis.observables.includes
export function includes<E>(collection: MutableObservableProperty<Set<E>>, element: E): MutableObservableProperty<boolean> {
    return comLightningkiteKhrysalisObservablesObservablePropertyWithWrite(comLightningkiteKhrysalisObservablesObservablePropertyMap(collection, (it) => it.has(element)), (it) => {
            if (it) {
                collection.value = new Set([...collection.value, element]);
            } else {
                collection.value = new Set([...collection.value].filter(x => x !== element));
            }
    });
}

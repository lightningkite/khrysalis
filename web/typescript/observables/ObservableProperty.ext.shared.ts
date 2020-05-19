// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/ObservableProperty.ext.shared.kt
// Package: com.lightningkite.khrysalis.observables
// FQImport: com.lightningkite.khrysalis.observables.observable TS getComLightningkiteKhrysalisObservablesObservablePropertyObservable
// FQImport: com.lightningkite.khrysalis.observables.includes.<anonymous>.it TS it
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.C TS C
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.onNext TS onNext
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.T TS T
// FQImport: kotlin.collections.minus TS kotlinCollectionsSetMinus
// FQImport: com.lightningkite.khrysalis.observables.includes.collection TS collection
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.<anonymous>.a TS a
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.<anonymous>.b TS b
// FQImport: kotlin.Throwable.printStackTrace TS printStackTrace
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.A TS A
// FQImport: kotlin.collections.Set TS Set
// FQImport: com.lightningkite.khrysalis.rx.addWeak TS ioReactivexObservableAddWeak
// FQImport: com.lightningkite.khrysalis.observables.withWrite TS comLightningkiteKhrysalisObservablesObservablePropertyWithWrite
// FQImport: com.lightningkite.khrysalis.observables.observableNN.T TS T
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.referenceA TS referenceA
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.onComplete TS onComplete
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.referenceC TS referenceC
// FQImport: com.lightningkite.khrysalis.observables.observable.T TS T
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.listener TS listener
// FQImport: com.lightningkite.khrysalis.observables.observable SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.T TS T
// FQImport: kotlin.collections.plus TS kotlinCollectionsSetPlus
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.<anonymous>.c TS c
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.onChange TS onChange
// FQImport: com.lightningkite.khrysalis.observables.map TS comLightningkiteKhrysalisObservablesObservablePropertyMap
// FQImport: com.lightningkite.khrysalis.observables.<get-observableNN>.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty TS ObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.onError TS onError
// FQImport: com.lightningkite.khrysalis.observables.includes.E TS E
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.<anonymous>.value TS value
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.referenceB TS referenceB
// FQImport: com.lightningkite.khrysalis.observables.onChangeNN.T TS T
// FQImport: com.lightningkite.khrysalis.observables.includes.element TS element
// FQImport: kotlin.Throwable TS Throwable
// FQImport: kotlin.collections.Set.contains TS contains
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.<anonymous>.boxed TS boxed
// FQImport: com.lightningkite.khrysalis.observables.addAndRunWeak.B TS B
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.<get-onChangeNN>.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty TS MutableObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.value TS value
import { comLightningkiteKhrysalisObservablesObservablePropertyWithWrite } from './WriteAddedObservableProperty.shared'
import { ObservableProperty } from './ObservableProperty.shared'
import { ioReactivexObservableAddWeak } from './../rx/RxAddAndRunWeak.shared'
import { comLightningkiteKhrysalisObservablesObservablePropertyMap } from './TransformedObservableProperty.shared'
import { Observable, SubscriptionLike, concat as rxConcat, of as rxOf } from 'rxjs'
import { map as rxMap } from 'rxjs/operators'
import { MutableObservableProperty } from './MutableObservableProperty.shared'

//! Declares com.lightningkite.khrysalis.observables.observable
export function getComLightningkiteKhrysalisObservablesObservablePropertyObservable<T>(this_: ObservableProperty<T>): Observable<T> { return rxConcat(rxOf(this_.value), this_.onChange); }

//! Declares com.lightningkite.khrysalis.observables.observableNN
export function getComLightningkiteKhrysalisObservablesObservablePropertyObservableNN<T>(this_: ObservableProperty<T>): Observable<T> { return rxConcat(rxOf(this_.value), this_.onChange).pipe(rxMap((it) => it)); }

//! Declares com.lightningkite.khrysalis.observables.onChangeNN
export function getComLightningkiteKhrysalisObservablesObservablePropertyOnChangeNN<T>(this_: ObservableProperty<T>): Observable<T> { return this_.onChange.pipe(rxMap((it) => it)); }


//! Declares com.lightningkite.khrysalis.observables.subscribeBy
export function comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy<T>(this_: ObservableProperty<T>, onError:  (a: Throwable) => void = (it) => {
        it.printStackTrace()
}, onComplete:  () => void = () => {}, onNext:  (a: T) => void = (it) => {}): SubscriptionLike{ return getComLightningkiteKhrysalisObservablesObservablePropertyObservable(this_).subscribe((boxed) => {
            onNext(boxed)
}, onError, onComplete); }

//! Declares com.lightningkite.khrysalis.observables.addAndRunWeak
export function comLightningkiteKhrysalisObservablesObservablePropertyAddAndRunWeak<A extends object, T>(this_: ObservableProperty<T>, referenceA: A, listener:  (a: A, b: T) => void): SubscriptionLike{ return ioReactivexObservableAddWeak(getComLightningkiteKhrysalisObservablesObservablePropertyObservable(this_), referenceA, (a, value) => {
            listener(
                a,
                value
            )
}); }

//! Declares com.lightningkite.khrysalis.observables.addAndRunWeak
export function comLightningkiteKhrysalisObservablesObservablePropertyAddAndRunWeak<A extends object, B extends object, T>(this_: ObservableProperty<T>, referenceA: A, referenceB: B, listener:  (a: A, b: B, c: T) => void): SubscriptionLike{ return ioReactivexObservableAddWeak(getComLightningkiteKhrysalisObservablesObservablePropertyObservable(this_), referenceA, referenceB, (a, b, value) => {
            listener(
                a,
                b,
                value
            )
}); }

//! Declares com.lightningkite.khrysalis.observables.addAndRunWeak
export function comLightningkiteKhrysalisObservablesObservablePropertyAddAndRunWeak<A extends object, B extends object, C extends object, T>(this_: ObservableProperty<T>, referenceA: A, referenceB: B, referenceC: C, listener:  (a: A, b: B, c: C, d: T) => void): SubscriptionLike{ return ioReactivexObservableAddWeak(getComLightningkiteKhrysalisObservablesObservablePropertyObservable(this_), referenceA, referenceB, referenceC, (a, b, c, value) => {
            listener(
                a,
                b,
                c,
                value
            )
}); }

//! Declares com.lightningkite.khrysalis.observables.includes
export function includes<E>(collection: MutableObservableProperty<Set<E>>, element: E): MutableObservableProperty<Boolean>{
    return comLightningkiteKhrysalisObservablesObservablePropertyWithWrite(comLightningkiteKhrysalisObservablesObservablePropertyMap(collection, (it) => it.contains(element)), (it) => {
            if (it) {
                collection.value = kotlinCollectionsSetPlus(collection.value, element);
            } else {
                collection.value = kotlinCollectionsSetMinus(collection.value, element);
            }
    });
}


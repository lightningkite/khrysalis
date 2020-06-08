// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/TransformedObservableProperty.shared.kt
// Package: com.lightningkite.khrysalis.observables
// FQImport: com.lightningkite.khrysalis.observables.TransformedObservableProperty.basedOn TS basedOn
// FQImport: com.lightningkite.khrysalis.observables.TransformedObservableProperty.A TS A
// FQImport: com.lightningkite.khrysalis.observables.transformed.T TS T
// FQImport: com.lightningkite.khrysalis.observables.TransformedObservableProperty.onChange.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.map.T TS T
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.onChange TS onChange
// FQImport: com.lightningkite.khrysalis.observables.TransformedObservableProperty TS TransformedObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.transformed.read TS read
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty TS ObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.map.read TS read
// FQImport: com.lightningkite.khrysalis.observables.TransformedObservableProperty.B TS B
// FQImport: com.lightningkite.khrysalis.observables.TransformedObservableProperty.read TS read
// FQImport: com.lightningkite.khrysalis.observables.transformed.B TS B
// FQImport: com.lightningkite.khrysalis.observables.map.B TS B
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.value TS value
import { ObservableProperty } from './ObservableProperty.shared'
import { Observable } from 'rxjs'
import { map as rxMap } from 'rxjs/operators'

//! Declares com.lightningkite.khrysalis.observables.TransformedObservableProperty
export class TransformedObservableProperty<A, B> extends ObservableProperty<any> {
    public readonly basedOn: ObservableProperty<A>;
    public readonly read:  (a: A) => B;
    public constructor(basedOn: ObservableProperty<A>, read:  (a: A) => B) {
        super();
        this.basedOn = basedOn;
        this.read = read;
        this.onChange = this.basedOn.onChange.pipe(rxMap((it) => this.read(it)));
    }
    
    //! Declares com.lightningkite.khrysalis.observables.TransformedObservableProperty.value
    public get value(): B {
        return this.read(this.basedOn.value);
    }
    
    public readonly onChange: Observable<B>;
    
}

//! Declares com.lightningkite.khrysalis.observables.transformed>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
export function comLightningkiteKhrysalisObservablesObservablePropertyTransformed<T, B>(this_: ObservableProperty<T>, read:  (a: T) => B): ObservableProperty<B> {
    return new TransformedObservableProperty<T, B>(this_, read);
}

//! Declares com.lightningkite.khrysalis.observables.map>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
export function comLightningkiteKhrysalisObservablesObservablePropertyMap<T, B>(this_: ObservableProperty<T>, read:  (a: T) => B): ObservableProperty<B> {
    return new TransformedObservableProperty<T, B>(this_, read);
}


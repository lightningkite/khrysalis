// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/TransformedMutableObservableProperty2.shared.kt
// Package: com.lightningkite.khrysalis.observables
import { Box, boxWrap } from './../Box.actual'
import { Observable } from 'rxjs'
import { map } from 'rxjs/operators as rxMap'
import { MutableObservableProperty } from './MutableObservableProperty.shared'
import { TransformedMutableObservableProperty2 } from './TransformedMutableObservableProperty2.shared'

//! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty2
export class TransformedMutableObservableProperty2<A, B> extends MutableObservableProperty<any> {
    public readonly basedOn: MutableObservableProperty<A>;
    public readonly read:  (a: A) => B;
    public readonly write:  (a: A, b: B) => A;
    public constructor( basedOn: MutableObservableProperty<A>,  read:  (a: A) => B,  write:  (a: A, b: B) => A) {
        super();
        this.basedOn = basedOn;
        this.read = read;
        this.write = write;
        this.onChange = rxMap((it) => boxWrap(this.read(it.value)))(basedOn.onChange);
    }
    
    public update(){
        basedOn.update();
    }
    
    //! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty2.value
    public get value(): B { return {
            return this.read(basedOn.value);
    }; }{
        return this.read(basedOn.value);
    }
    public set value(value: B) {
        basedOn.value = this.write(basedOn.value, value);
    }
    
    public readonly onChange: Observable<Box<B>> = rxMap((it) => boxWrap(this.read(it.value)))(basedOn.onChange);
    
}

//! Declares com.lightningkite.khrysalis.observables.mapWithExisting
export function ComLightningkiteKhrysalisObservablesMutableObservablePropertyMapWithExisting<T, B>(this_MapWithExisting: MutableObservableProperty<T>, read:  (a: T) => B, write:  (a: T, b: B) => T): MutableObservableProperty<B>{
    return new TransformedMutableObservableProperty2<T, B>(this_MapWithExisting, read, write);
}


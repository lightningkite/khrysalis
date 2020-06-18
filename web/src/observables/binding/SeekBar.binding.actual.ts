// Generated by Khrysalis TypeScript converter
// File: observables/binding/SeekBar.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from '../ObservableProperty.ext.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from '../../rx/DisposeCondition.actual'
import { MutableObservableProperty } from '../MutableObservableProperty.shared'

//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.SeekBar
export function androidWidgetSeekBarBind(this_: HTMLInputElement, start: number, endInclusive: number, observable: MutableObservableProperty<number>): void {
    this_.min = start.toString()
    this_.max = endInclusive.toString()

    let suppress = false;
    
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (value) => {
                if (!suppress) {
                    suppress = true;
                    this_.valueAsNumber = (value);
                    suppress = false;
                }
    }), getAndroidViewViewRemoved(this_));
    this_.onchange = (e) => {
        if(!suppress){
            suppress = true;
            observable.value = start;
            suppress = false;
        }
    }
}

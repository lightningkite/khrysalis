// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/Button.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.rx.until TS ioReactivexDisposablesDisposableUntil
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.inactiveBackground TS inactiveBackground
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.observable TS observable
// FQImport: isEnabled TS setAndroidWidgetButtonIsEnabled
// FQImport: android.graphics.drawable.Drawable TS Drawable
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.activeColorResource TS activeColorResource
// FQImport: android.widget.Button TS Button
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.<anonymous>.<anonymous>.color TS color
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty TS ObservableProperty
// FQImport: android.widget.Button.setBackgroundResource TS setBackgroundResource
// FQImport: com.lightningkite.khrysalis.views.backgroundDrawable TS setAndroidViewViewBackgroundDrawable
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy TS comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy
// FQImport: com.lightningkite.khrysalis.views.ColorResource TS ColorResource
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.activeBackground TS activeBackground
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.inactiveColorResource TS inactiveColorResource
// FQImport: com.lightningkite.khrysalis.rx.removed TS getAndroidViewViewRemoved
// FQImport: com.lightningkite.khrysalis.observables.binding.bindActive.<anonymous>.it TS it
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from './../ObservableProperty.ext.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from './../../rx/DisposeCondition.actual'
import { ObservableProperty } from './../ObservableProperty.shared'
import { setAndroidViewViewBackgroundDrawable } from './../../views/View.ext.actual'
import { ColorResource } from './../../views/ResourceTypes.actual'

//! Declares com.lightningkite.khrysalis.observables.binding.bindActive
export function androidWidgetButtonBindActive(this_BindActive: Button, observable: ObservableProperty<Boolean>, activeColorResource: (ColorResource | null) = null, inactiveColorResource: (ColorResource | null) = null){
    return ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (it) => {
                setAndroidWidgetButtonIsEnabled(this_BindActive, it);
                (() => {if (it) {
                            const temp360 = activeColorResource;
                            if(temp360 !== null) ((color) => this_BindActive.setBackgroundResource(color))(temp360);
                        } else {
                            const temp362 = inactiveColorResource;
                            if(temp362 !== null) ((color) => this_BindActive.setBackgroundResource(color))(temp362);
                }})()
    }), getAndroidViewViewRemoved(this_BindActive));
}


//! Declares com.lightningkite.khrysalis.observables.binding.bindActive
export function androidWidgetButtonBindActive(this_BindActive: Button, observable: ObservableProperty<Boolean>, activeBackground: Drawable, inactiveBackground: Drawable){
    return ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(observable, undefined, undefined, (it) => {
                setAndroidWidgetButtonIsEnabled(this_BindActive, it);
                (() => {if (it) {
                            setAndroidViewViewBackgroundDrawable(this_BindActive, activeBackground);
                        } else {
                            
                            setAndroidViewViewBackgroundDrawable(this_BindActive, inactiveBackground);
                }})()
    }), getAndroidViewViewRemoved(this_BindActive));
}


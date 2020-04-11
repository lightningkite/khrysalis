package com.lightningkite.khrysalis.observables.binding

import android.widget.CompoundButton
import android.widget.ToggleButton
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.subscribeBy
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


/**
 *
 * Binds the textOn value in the toggle button to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindOnString(text)
 *
 */

fun ToggleButton.bindOnString(observable: ObservableProperty<String>) {
    observable.subscribeBy { value ->
        this.textOn = value
        if(this.isChecked){
            this.text = value
        }
    }.until(this.removed)
}

/**
 *
 * Binds the textOff value in the toggle button to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindOffString(text)
 *
 */

fun ToggleButton.bindOffString(observable: ObservableProperty<String>) {
    observable.subscribeBy { value ->
        this.textOff = value
        if(!this.isChecked){
            this.text = value
        }
    }.until(this.removed)
}

/**
 *
 * Binds both the textOff and textOn values in the toggle button to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindOnOffString(text)
 *
 */

fun ToggleButton.bindOnOffString(observable: ObservableProperty<String>) {
    observable.subscribeBy { value ->
        this.textOff = value
        this.textOn = value
        this.text = value
    }.until(this.removed)
}
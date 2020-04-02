package com.lightningkite.khrysalis.observables.binding

import android.widget.CompoundButton
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


/**
 *
 * Binds the checked state of the compound button to be checked if the observable value matches the initial value provided.
 * If the button is tapped, it will change the observable value to be the value provided.
 * If the observable value is changed the button check state will update.
 *
 * Example
 * val selected = StandardObservableProperty<Int>(1)
 * button.bindSelect(1, selected)
 * If selected has a value of 1 the button is checked, otherwise it is unchecked.
 *
 */
fun <T> CompoundButton.bindSelect(value: T, observable: MutableObservableProperty<T>) {
    observable.subscribeBy { it ->
        val shouldBeChecked = it == value
        if (this.isChecked != shouldBeChecked) {
            this.isChecked = shouldBeChecked
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && observable.value != value) {
            observable.value = value
        } else if (!isChecked && observable.value == value) {
            this.isChecked = true
        }
    }
}


/**
 *
 * Binds the checked state of the compound button to be checked if the observable value matches the initial value provided.
 * If the button is tapped, it will change the observable value to be the value provided.
 * If the observable value is changed the button check state will update
 * This Nullable however allows for the Observable to be null. If null the button is UNCHECKED
 *
 * Example
 * val selected = StandardObservableProperty<Int>(1)
 * button.bindSelect(1, selected)
 * If selected has a value of 1 the button is checked, otherwise it is unchecked.
 *
 */

fun <T> CompoundButton.bindSelectNullable(value: T, observable: MutableObservableProperty<T?>) {
    observable.subscribeBy { it ->
        val shouldBeChecked = it == value
        if (this.isChecked != shouldBeChecked) {
            this.isChecked = shouldBeChecked
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && observable.value != value) {
            observable.value = value
        } else if (!isChecked && observable.value == value) {
            observable.value = null
        }
    }
}




/**
 *
 * Binds the checked state of the compound button to be checked if the observable value matches the initial value provided or null.
 * The observable is allowed to be null, and the button will be marked as checked if it is null.
 * If the button is tapped, it will change the observable value to be the value provided.
 * If the observable value is changed the button check state will update.
 *
 * Example
 * val selected = StandardObservableProperty<Int>(1)
 * button.bindSelect(1, selected)
 * If selected has a value of 1 or null the button is checked, otherwise it is unchecked.
 *
 */
fun <T> CompoundButton.bindSelectInvert(value: T, observable: MutableObservableProperty<T?>) {
    var suppress = false
    observable.subscribeBy { it ->
        if (!suppress) {
            suppress = true
            val shouldBeChecked = it == value || it == null
            if (this.isChecked != shouldBeChecked) {
                this.isChecked = shouldBeChecked
            }
            suppress = false
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (!suppress) {
            suppress = true
            if (!isChecked && observable.value == value) {
                observable.value = null
                buttonView.isChecked = true
            } else if (observable.value != value) {
                observable.value = value
                buttonView.isChecked = true
            }
            suppress = false
        }
    }
}

/**
 *
 * Binds the checked state of the compound button to the observable value.
 * If the button is tapped, it will change the observable value.
 * If the observable value is changed the button check state will update to match.
 *
 *
 * Example
 * val selected = StandardObservableProperty<Boolean>(false)
 * button.bindSelect(selected)
 * If selected is true the button is checked, otherwise it is unchecked.
 *
 */
fun CompoundButton.bind(observable: MutableObservableProperty<Boolean>) {
    observable.subscribeBy { it ->
        if (it != this.isChecked) {
            this.isChecked = it
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (observable.value != isChecked) {
            observable.value = isChecked
        }
    }
}

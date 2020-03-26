package com.lightningkite.khrysalis.observables.binding

import android.widget.CompoundButton
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


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

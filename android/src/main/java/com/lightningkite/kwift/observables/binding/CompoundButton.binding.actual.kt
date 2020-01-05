package com.lightningkite.kwift.observables.binding

import android.widget.CompoundButton
import com.lightningkite.kwift.observables.*


fun <T> CompoundButton.bindSelect(value: T, observable: MutableObservableProperty<T>) {
    observable.addAndRunWeak(this) { self, it ->
        val shouldBeChecked = it == value
        if (self.isChecked != shouldBeChecked) {
            self.isChecked = shouldBeChecked
        }
    }
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && observable.value != value) {
            observable.value = value
        } else if (!isChecked && observable.value == value) {
            this.isChecked = true
        }
    }
}

fun <T> CompoundButton.bindSelectNullable(value: T, observable: MutableObservableProperty<T?>) {
    observable.addAndRunWeak(this) { self, it ->
        val shouldBeChecked = it == value
        if (self.isChecked != shouldBeChecked) {
            self.isChecked = shouldBeChecked
        }
    }
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
    observable.addAndRunWeak(this) { self, it ->
        if (!suppress) {
            suppress = true
            val shouldBeChecked = it == value || it == null
            if (self.isChecked != shouldBeChecked) {
                self.isChecked = shouldBeChecked
            }
            suppress = false
        }
    }
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
    observable.addAndRunWeak(this) { self, it ->
        if (it != this.isChecked) {
            self.isChecked = it
        }
    }
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (observable.value != isChecked) {
            observable.value = isChecked
        }
    }
}

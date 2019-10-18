package com.lightningkite.kwift.observables.actual

import android.widget.CompoundButton
import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak


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

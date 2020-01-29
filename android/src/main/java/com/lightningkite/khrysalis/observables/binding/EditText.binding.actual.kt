package com.lightningkite.khrysalis.observables.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.lightningkite.khrysalis.observables.*


fun EditText.bindString(observable: MutableObservableProperty<String>) {
    observable.addAndRunWeak(this) { self, value ->
        if (observable.value != text.toString()) {
            this.setText(observable.value)
        }
    }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (observable.value != s) {
                observable.value = (s.toString())
            }
        }
    })
}

fun EditText.bindInteger(observable: MutableObservableProperty<Int>) {
    observable.addAndRunWeak(this) { self, value ->
        val currentValue = self.text.toString().toIntOrNull()
        if (currentValue != null && observable.value != currentValue) {
            self.setText(observable.value.takeUnless { it == 0 }?.toString() ?: "")
        }
    }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toIntOrNull()
            if (currentValue != null && observable.value != currentValue) {
                observable.value = currentValue
            }
        }
    })
}

fun EditText.bindDouble(observable: MutableObservableProperty<Double>) {
    observable.addAndRunWeak(this) { self, value ->
        val currentValue = self.text.toString().toDoubleOrNull()
        if (currentValue != null && observable.value != currentValue) {
            self.setText(observable.value.takeUnless { it == 0.0 }?.toString() ?: "")
        }
    }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toDoubleOrNull()
            if (currentValue != null && observable.value != currentValue) {
                observable.value = currentValue
            }
        }
    })
}

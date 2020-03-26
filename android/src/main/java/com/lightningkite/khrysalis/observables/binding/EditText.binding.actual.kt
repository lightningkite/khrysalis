package com.lightningkite.khrysalis.observables.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


fun EditText.bindString(observable: MutableObservableProperty<String>) {
    observable.subscribeBy { value ->
        if (observable.value != text.toString()) {
            this.setText(observable.value)
        }
    }.until(this.removed)
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
    observable.subscribeBy { value ->
        val currentValue = this.text.toString().toIntOrNull()
        if (currentValue != null && observable.value != currentValue) {
            this.setText(observable.value.takeUnless { it == 0 }?.toString() ?: "")
        }
    }.until(this.removed)
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
    observable.subscribeBy { value ->
        val currentValue = this.text.toString().toDoubleOrNull()
        if (currentValue != null && observable.value != currentValue) {
            this.setText(observable.value.takeUnless { it == 0.0 }?.toString() ?: "")
        }
    }.until(this.removed)
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

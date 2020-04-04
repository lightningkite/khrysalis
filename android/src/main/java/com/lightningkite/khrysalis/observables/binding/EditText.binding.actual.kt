package com.lightningkite.khrysalis.observables.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

/**
 *
 * Binds the edit text internal text to the ObservableProperty provided.
 * Any changes made by the user to the edit text will update the value of the observable.
 * Any changes directly to the observable will manifest in the edit text.
 *
 * Example
 * val name = StandardObservableProperty("Jason")
 * nameField.bindString(name)
 * The name field will be populated with "Jason"
 *
 */
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

/**
 *
 * Binds the edit text internal text to the ObservableProperty provided.
 * This also limits the input type to a valid integer.
 * Any changes made by the user to the edit text will update the value of the observable.
 * Any changes directly to the observable will manifest in the edit text.
 *
 * Example
 * val value = StandardObservableProperty(23)
 * nameField.bindString(value)
 * The name field will be populated with "23"
 *
 */
fun EditText.bindInteger(observable: MutableObservableProperty<Int>) {
    observable.subscribeBy { value ->
        val currentValue = this.text.toString().toIntOrNull()
        if (value != currentValue) {
            this.setText(value.takeUnless { it == 0 }?.toString() ?: "")
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toIntOrNull() ?: 0
            if (observable.value != currentValue) {
                observable.value = currentValue
            }
        }
    })
}



/**
 *
 * Binds the edit text internal text to the ObservableProperty provided.
 * This also limits the input type to a valid double.
 * Any changes made by the user to the edit text will update the value of the observable.
 * Any changes directly to the observable will manifest in the edit text.
 *
 * Example
 * val value = StandardObservableProperty(23.3)
 * nameField.bindString(value)
 * The name field will be populated with "23.3"
 *
 */
fun EditText.bindDouble(observable: MutableObservableProperty<Double>) {
    observable.subscribeBy { value ->
        val currentValue = this.text.toString().toDoubleOrNull()
        if (value != currentValue) {
            this.setText(value.takeUnless { it == 0.0 }?.toString() ?: "")
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toDoubleOrNull() ?: 0.0
            if (observable.value != currentValue) {
                observable.value = currentValue
            }
        }
    })
}

package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.widget.TextView
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.StringResource

/**
 *
 * Binds the text in the text view to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindString(text)
 *
 */

fun TextView.bindString(observable: ObservableProperty<String>) {
    observable.subscribeBy { value ->
        this.text = value
    }.until(this.removed)
}

/**
 *
 * Binds the text in the text view to the string resource provided in the observable
 *
 * Example
 * val text = StandardObservableProperty(R.string.test_text)
 * view.bindString(text)
 *
 */
fun TextView.bindStringRes(observable: ObservableProperty<StringResource?>) {
    observable.subscribeBy { value ->
        this.visibility = if (value == null) View.GONE else View.VISIBLE
        if (value != null) {
            this.text = this.resources.getString(value)
        }
    }.until(this.removed)
}


/**
 *
 * Binds the text in the text view to the string returned by the transform function
 * The transform function is the lambda that return a string when provided the value from the observable
 *
 * Example
 * val item = StandardObservableProperty(Item())
 * view.bindString(item){ item -> return "test"}
 *
 */
fun <T> TextView.bindText(observable: ObservableProperty<T>, transform: (T) -> String) {
    observable.subscribeBy { value ->
        this.text = transform(value)
    }.until(this.removed)
}

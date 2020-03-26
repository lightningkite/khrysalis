package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.widget.TextView
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.StringResource


fun TextView.bindString(observable: ObservableProperty<String>) {
    observable.subscribeBy { value ->
        this.text = value
    }.until(this.removed)
}

fun TextView.bindStringRes(observable: ObservableProperty<StringResource?>) {
    observable.subscribeBy { value ->
        this.visibility = if (value == null) View.GONE else View.VISIBLE
        if (value != null) {
            this.text = this.resources.getString(value)
        }
    }.until(this.removed)
}

fun <T> TextView.bindText(observable: ObservableProperty<T>, transform: (T) -> String) {
    observable.subscribeBy { value ->
        this.text = transform(value)
    }.until(this.removed)
}

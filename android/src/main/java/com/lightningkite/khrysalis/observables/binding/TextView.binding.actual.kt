package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.widget.TextView
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.views.StringResource


fun TextView.bindString(observable: ObservableProperty<String>) {
    observable.addAndRunWeak(this) { self, value ->
        self.text = value
    }
}

fun TextView.bindStringRes(observable: ObservableProperty<StringResource?>) {
    observable.addAndRunWeak(this) { self, value ->
        self.visibility = if (value == null) View.GONE else View.VISIBLE
        if (value != null) {
            self.text = self.resources.getString(value)
        }
    }
}

fun <T> TextView.bindText(observable: ObservableProperty<T>, transform: (T) -> String) {
    observable.addAndRunWeak(this) { self, value ->
        self.text = transform(value)
    }
}

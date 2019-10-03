package com.lightningkite.kwift.observables.actual

import android.view.View
import com.lightningkite.kwift.observables.shared.ObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak


fun View.bindVisible(observable: ObservableProperty<Boolean>) {
    observable.addAndRunWeak(this) { self, value ->
        self.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }
}

fun View.bindExists(observable: ObservableProperty<Boolean>) {
    observable.addAndRunWeak(this) { self, value ->
        self.visibility = if (value) View.VISIBLE else View.GONE
    }
}

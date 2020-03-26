package com.lightningkite.khrysalis.observables.binding

import android.view.View
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


fun View.bindVisible(observable: ObservableProperty<Boolean>) {
    observable.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }.until(this.removed)
}

fun View.bindExists(observable: ObservableProperty<Boolean>) {
    observable.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.GONE
    }.until(this.removed)
}

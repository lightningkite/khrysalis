package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.AnyObject
import com.lightningkite.kwift.Equatable
import com.lightningkite.kwift.observables.MutableObservableProperty
import com.lightningkite.kwift.observables.addAndRunWeak
import com.lightningkite.kwift.observables.addWeak

fun <T> MutableObservableProperty<T>.serves(whilePresent: AnyObject, other: MutableObservableProperty<T>) {

    var suppress = false

    other.addAndRunWeak(whilePresent){ ignored, value ->
        if (!suppress) {
            suppress = true
            this.value = value
            suppress = false
        }
    }

    this.onChange.addWeak(whilePresent){ ignored, value ->
        if (!suppress) {
            suppress = true
            other.value = value
            suppress = false
        }
    }
}

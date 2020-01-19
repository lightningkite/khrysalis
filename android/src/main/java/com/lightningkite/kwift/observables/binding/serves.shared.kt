package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.AnyObject
import com.lightningkite.kwift.observables.MutableObservableProperty
import com.lightningkite.kwift.observables.observable
import com.lightningkite.kwift.rx.addWeak

fun <T> MutableObservableProperty<T>.serves(whilePresent: AnyObject, other: MutableObservableProperty<T>) {

    var suppress = false

    other.observable.addWeak(whilePresent, { ignored, value ->
        if (!suppress) {
            suppress = true
            this.value = value.value
            suppress = false
        }
    })

    this.onChange.addWeak(whilePresent, { ignored, value ->
        if (!suppress) {
            suppress = true
            other.value = value.value
            suppress = false
        }
    })
}

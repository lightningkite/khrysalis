package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.AnyObject
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.observable
import com.lightningkite.khrysalis.rx.addWeak

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

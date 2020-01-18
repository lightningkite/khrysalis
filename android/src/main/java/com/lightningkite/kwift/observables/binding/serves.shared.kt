package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.AnyObject
import com.lightningkite.kwift.Equatable
import com.lightningkite.kwift.Optional
import com.lightningkite.kwift.observables.MutableObservableProperty
import com.lightningkite.kwift.observables.addAndRunWeak
import com.lightningkite.kwift.observables.observable
import com.lightningkite.kwift.rx.addWeak

fun <T> MutableObservableProperty<T>.serves(whilePresent: AnyObject, other: MutableObservableProperty<T>) {

    var suppress = false

    other.observable.addWeak(whilePresent, { ignored, value ->
        if (!suppress) {
            suppress = true
            this.value = value.valueNN
            suppress = false
        }
    })

    this.onChange.addWeak<AnyObject, Optional<T>>(whilePresent, { ignored, value ->
        if (!suppress) {
            suppress = true
            other.value = value.valueNN
            suppress = false
        }
    })
}

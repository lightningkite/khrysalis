package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.weak
import io.reactivex.Observable

class EventToObservableProperty<T>(override var value: T, val wrapped: Observable<Optional<T>>): ObservableProperty<T>() {
    @Suppress("UNCHECKED_CAST")
    override val onChange: Observable<Optional<T>> get() = wrapped.map { it ->
        this.value = it.value as T
        return@map it
    }
}

fun <T> Observable<Optional<T>>.asObservableProperty(defaultValue: T): ObservableProperty<T> {
    return EventToObservableProperty<T>(defaultValue, this)
}


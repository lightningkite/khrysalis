package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.swiftExactly
import io.reactivex.Observable

class EventToObservableProperty<T>(override var value: T, val wrapped: Observable<Box<T>>): ObservableProperty<T>() {
    override val onChange: Observable<Box<T>> get() = wrapped.map { it ->
        this.value = it.value
        return@map it
    }
}
fun <Element> Observable<@swiftExactly("Element") Element>.asObservableProperty(defaultValue: Element): ObservableProperty<Element> {
    return EventToObservableProperty<Element>(defaultValue, this.map { it -> boxWrap(it) })
}

fun <Element> Observable<@swiftExactly("Element") Element>.asObservablePropertyDefaultNull(): ObservableProperty<Element?> {
    return EventToObservableProperty<Element?>(null, this.map { it -> boxWrap(it) })
}


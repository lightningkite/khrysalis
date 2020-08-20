package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.*
import io.reactivex.Observable

class EventToObservableProperty<T>(override var value: T, val wrapped: Observable<Box<T>>) : ObservableProperty<T>() {
    override val onChange: Observable<Box<T>>
        get() = Observable.concat(wrapped.map { it ->
            value = it.value
            it
        }.doOnError {
            Log.e(
                "EventToObservableProperty",
                "Oh boy, you done screwed up.  The following stack trace is from an Observable that had an error that was converted to an ObservableProperty, which has a contract to never error.  The currently held value is '$value"
            )
            it.printStackTrace()
        }.onErrorResumeNext(Observable.never()), Observable.never())
}

fun <Element> Observable<@SwiftExactly("Element") Element>.asObservableProperty(defaultValue: Element): ObservableProperty<Element> {
    return EventToObservableProperty<Element>(defaultValue, this.map { it -> boxWrap(it) })
}

fun <Element> Observable<@SwiftExactly("Element") Element>.asObservablePropertyDefaultNull(): ObservableProperty<Element?> {
    return EventToObservableProperty<Element?>(null, this.map { it -> boxWrap(it) })
}


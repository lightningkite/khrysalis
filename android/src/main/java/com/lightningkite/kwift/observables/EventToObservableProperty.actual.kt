package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.swiftExactly
import io.reactivex.Observable


fun <T> Observable<Box<T>>.asObservablePropertyUnboxed(defaultValue: T): ObservableProperty<T> {
    return EventToObservableProperty<T>(defaultValue, this)
}

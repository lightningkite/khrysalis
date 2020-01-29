package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.swiftExactly
import io.reactivex.Observable


fun <T> Observable<Box<T>>.asObservablePropertyUnboxed(defaultValue: T): ObservableProperty<T> {
    return EventToObservableProperty<T>(defaultValue, this)
}

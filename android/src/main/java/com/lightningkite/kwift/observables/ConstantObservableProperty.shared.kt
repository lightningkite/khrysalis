package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import io.reactivex.Observable

class ConstantObservableProperty<T>(val underlyingValue: T) : ObservableProperty<T>() {
    override val onChange: Observable<Box<T>> = Observable.never()
    override val value: T
        get() = underlyingValue
}

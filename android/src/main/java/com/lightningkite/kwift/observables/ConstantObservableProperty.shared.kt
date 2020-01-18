package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import io.reactivex.Observable
import java.util.*

class ConstantObservableProperty<T>(val underlyingValue: T) : ObservableProperty<T>() {
    override val onChange: Observable<Optional<T>> = Observable.never()
    override val value: T
        get() = underlyingValue
}

package com.lightningkite.kwift.observables

class ConstantObservableProperty<T>(val underlyingValue: T) : ObservableProperty<T>() {
    override val onChange: NeverEvent<T> = NeverEvent<T>()
    override val value: T
        get() = underlyingValue
}

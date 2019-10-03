package com.lightningkite.kwift.observables.shared

class StandardObservableProperty<T>(var underlyingValue: T) : MutableObservableProperty<T>() {
    override val onChange: StandardEvent<T> = StandardEvent<T>()
    override var value: T
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            onChange.invokeAll(value = value)
        }
}

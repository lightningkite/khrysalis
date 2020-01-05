package com.lightningkite.kwift.observables

class StandardObservableProperty<T>(var underlyingValue: T) : MutableObservableProperty<T>() {
    override val onChange: StandardEvent<T> = StandardEvent<T>()
    override var value: T
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            onChange.invokeAll(value = value)
        }
    override fun update() {
        onChange.invokeAll(value = value)
    }
}

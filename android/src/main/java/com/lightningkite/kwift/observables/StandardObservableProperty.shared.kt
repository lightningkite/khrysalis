package com.lightningkite.kwift.observables

class StandardObservableProperty<T>(var underlyingValue: T, override val onChange: InvokableEvent<T> = StandardEvent<T>()) : MutableObservableProperty<T>() {
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

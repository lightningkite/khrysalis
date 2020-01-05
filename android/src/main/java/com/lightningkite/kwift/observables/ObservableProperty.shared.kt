package com.lightningkite.kwift.observables

abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Event<T>
}


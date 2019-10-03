package com.lightningkite.kwift.observables.shared

abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Event<T>
}


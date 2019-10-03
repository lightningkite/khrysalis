package com.lightningkite.kwift.observables.shared

abstract class MutableObservableProperty<T> : ObservableProperty<T>() {
    abstract override var value: T
}

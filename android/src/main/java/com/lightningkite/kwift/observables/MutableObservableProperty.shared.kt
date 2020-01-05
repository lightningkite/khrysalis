package com.lightningkite.kwift.observables

abstract class MutableObservableProperty<T> : ObservableProperty<T>() {
    abstract override var value: T
    abstract fun update()
}

package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import io.reactivex.Observable
import io.reactivex.subjects.Subject

abstract class MutableObservableProperty<T> : ObservableProperty<T>() {
    abstract override var value: T
    abstract fun update()
}

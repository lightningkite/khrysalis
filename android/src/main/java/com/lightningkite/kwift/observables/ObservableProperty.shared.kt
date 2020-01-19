package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import io.reactivex.Observable

abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Observable<Box<T>>
}


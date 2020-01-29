package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import io.reactivex.Observable

abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Observable<Box<T>>
}


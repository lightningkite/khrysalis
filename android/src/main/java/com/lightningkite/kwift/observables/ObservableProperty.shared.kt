package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import io.reactivex.Observable
import java.util.*

abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Observable<Optional<T>>
}


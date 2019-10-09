package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping

abstract class Event<T> {
    abstract fun add(listener: @escaping() (T) -> Boolean): Close
}

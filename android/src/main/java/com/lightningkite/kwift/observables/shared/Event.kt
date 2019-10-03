package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.escaping

abstract class Event<T> {
    abstract fun add(listener: @escaping() (T) -> Boolean): Close
}

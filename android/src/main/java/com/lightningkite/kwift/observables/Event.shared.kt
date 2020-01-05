package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

abstract class Event<T> {
    abstract fun add(listener: @escaping() (T) -> Boolean): Close
}

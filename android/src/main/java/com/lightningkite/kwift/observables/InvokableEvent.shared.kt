package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

abstract class InvokableEvent<T>: Event<T>() {
    abstract fun invokeAll(value: T)
}

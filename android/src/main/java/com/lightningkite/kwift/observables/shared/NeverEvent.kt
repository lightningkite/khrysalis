package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping

class NeverEvent<T> : Event<T>() {
    override fun add(listener: @escaping() (T) -> Boolean): Close {
        return Close({})
    }

}

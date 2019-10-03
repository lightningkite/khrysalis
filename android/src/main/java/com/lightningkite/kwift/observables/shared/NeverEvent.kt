package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.escaping

class NeverEvent<T> : Event<T>() {
    override fun add(listener: @escaping() (T) -> Boolean): Close {
        return Close({})
    }

}

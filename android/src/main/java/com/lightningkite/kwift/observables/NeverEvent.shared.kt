package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

class NeverEvent<T> : Event<T>() {
    override fun add(listener: @escaping() (T) -> Boolean): Close {
        return Close({})
    }

}

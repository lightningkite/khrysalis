package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

class ReferenceObservableProperty<T>(
    val get: @escaping() ()->T,
    val set: @escaping() (T)->Unit,
    val event: Event<T>
) : MutableObservableProperty<T>() {

    override val onChange: Event<T>
        get() = event
    override var value: T
        get() = this.get()
        set(value) {
            this.set(value)
        }
    override fun update() {
        //do nothing
    }
}

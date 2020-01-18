package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import com.lightningkite.kwift.escaping
import io.reactivex.Observable

class ReferenceObservableProperty<T>(
    val get: @escaping() ()->T,
    val set: @escaping() (T)->Unit,
    val event: Observable<Optional<T>>
) : MutableObservableProperty<T>() {

    override val onChange: Observable<Optional<T>>
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

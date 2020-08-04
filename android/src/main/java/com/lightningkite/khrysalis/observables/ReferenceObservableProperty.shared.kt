package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.Escaping
import io.reactivex.Observable

class ReferenceObservableProperty<T>(
    val get: @Escaping() ()->T,
    val set: @Escaping() (T)->Unit,
    val event: Observable<Box<T>>
) : MutableObservableProperty<T>() {

    override val onChange: Observable<Box<T>>
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

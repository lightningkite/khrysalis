package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.escaping
import io.reactivex.Observable

class WriteAddedObservableProperty<A>(
    val basedOn: ObservableProperty<A>,
    val onWrite: @escaping() (A) -> Unit
) : MutableObservableProperty<A>() {
    override var value: A
        get() = basedOn.value
        set(value) {
            onWrite(value)
        }
    override val onChange: Observable<Box<A>> get() = basedOn.onChange
    override fun update() {
        //Do nothing
    }
}

fun <T> ObservableProperty<T>.withWrite(
    onWrite: @escaping() (T) -> Unit
): MutableObservableProperty<T> {
    return WriteAddedObservableProperty<T>(this, onWrite)
}

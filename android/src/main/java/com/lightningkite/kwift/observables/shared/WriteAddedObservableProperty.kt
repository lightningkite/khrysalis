package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping

class WriteAddedObservableProperty<A>(
    val basedOn: ObservableProperty<A>,
    val onWrite: @escaping() (A) -> Unit
) : MutableObservableProperty<A>() {
    override fun update() {
        onWrite(basedOn.value)
    }
    override var value: A
        get() = basedOn.value
        set(value) {
            onWrite(value)
        }
    override val onChange: Event<A> get() = basedOn.onChange
}

fun <T> ObservableProperty<T>.withWrite(
    onWrite: @escaping() (T) -> Unit
): MutableObservableProperty<T> {
    return WriteAddedObservableProperty<T>(this, onWrite)
}

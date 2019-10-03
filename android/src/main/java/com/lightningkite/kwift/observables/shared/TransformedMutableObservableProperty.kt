package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.escaping

class TransformedMutableObservableProperty<A, B>(
    val basedOn: MutableObservableProperty<A>,
    val read: @escaping() (A) -> B,
    val write: @escaping() (B) -> A
) : MutableObservableProperty<B>() {
    override var value: B
        get() {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(value)
        }
    override val onChange: Event<B> = basedOn.onChange.transformed(transformation = read)
}

fun <T, B> MutableObservableProperty<T>.transformed(
    read: @escaping() (T) -> B,
    write: @escaping() (B) -> T
): MutableObservableProperty<B> {
    return TransformedMutableObservableProperty<T, B>(this, read, write)
}

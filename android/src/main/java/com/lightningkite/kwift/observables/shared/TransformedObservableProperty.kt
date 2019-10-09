package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping

class TransformedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val read: @escaping() (A) -> B
) : ObservableProperty<B>() {
    override val value: B
        get() {
            return read(basedOn.value)
        }
    override val onChange: Event<B> = basedOn.onChange.transformed(transformation = read)
}

fun <T, B> ObservableProperty<T>.transformed(read: @escaping() (T) -> B): ObservableProperty<B> {
    return TransformedObservableProperty<T, B>(this, read)
}

package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

fun <T, B, C> ObservableProperty<T>.combine(
    other: ObservableProperty<B>,
    combiner: @escaping() (T, B) -> C
): ObservableProperty<C> {
    return flatMap { av -> other.map { bv -> combiner(av, bv) } }
}

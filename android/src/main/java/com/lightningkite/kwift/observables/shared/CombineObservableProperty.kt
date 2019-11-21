package com.lightningkite.kwift.observables.shared

fun <T, B, C> ObservableProperty<T>.combine(
    other: ObservableProperty<B>,
    combiner: (T, B) -> C
): ObservableProperty<C> {
    return flatMap { av -> other.map { bv -> combiner(av, bv) } }
}

package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.rx.combineLatest
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.combineLatest

class CombineObservableProperty<T, A, B>(
    val observableA: ObservableProperty<A>,
    val observableB: ObservableProperty<B>,
    val combiner: @escaping() (A, B) -> T
): ObservableProperty<T>() {
    override val value: T
        get() = combiner(observableA.value, observableB.value)
    @Suppress("UNCHECKED_CAST")
    override val onChange: Observable<Optional<T>>
        get() = observableA.onChange.combineLatest(observableB.onChange) { a, b -> Optional.wrap(combiner(a.value as A, b.value as B)) }

}

fun <T, B, C> ObservableProperty<T>.combine(
    other: ObservableProperty<B>,
    combiner: @escaping() (T, B) -> C
): ObservableProperty<C> {
    return flatMap { av -> other.map { bv -> combiner(av, bv) } }
}

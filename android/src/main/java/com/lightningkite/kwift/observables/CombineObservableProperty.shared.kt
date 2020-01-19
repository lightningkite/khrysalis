package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.rx.combineLatest
import io.reactivex.Observable

class CombineObservableProperty<T, A, B>(
    val observableA: ObservableProperty<A>,
    val observableB: ObservableProperty<B>,
    val combiner: @escaping() (A, B) -> T
): ObservableProperty<T>() {
    override val value: T
        get() = combiner(observableA.value, observableB.value)
    override val onChange: Observable<Box<T>>
        get() = observableA.onChange.combineLatest(observableB.onChange) { a: Box<A>, b: Box<B> -> boxWrap(combiner(a.value, b.value)) }

}

fun <T, B, C> ObservableProperty<T>.combine(
    other: ObservableProperty<B>,
    combiner: @escaping() (T, B) -> C
): ObservableProperty<C> {
    return CombineObservableProperty(this, other, combiner)
}

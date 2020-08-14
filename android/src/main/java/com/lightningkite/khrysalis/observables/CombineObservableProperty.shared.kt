package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.CaptureUnowned
import com.lightningkite.khrysalis.Escaping
import com.lightningkite.khrysalis.boxWrap
import com.lightningkite.khrysalis.rx.combineLatest
import io.reactivex.Observable

class CombineObservableProperty<T, A, B>(
    val observableA: ObservableProperty<A>,
    val observableB: ObservableProperty<B>,
    val combiner: @Escaping() (A, B) -> T
): ObservableProperty<T>() {
    override val value: T
        get() = combiner(observableA.value, observableB.value)
    override val onChange: Observable<Box<T>>
        get() {
            val combinerCopy = combiner
            return observableA.onChange.startWith(Box.wrap(observableA.value))
                .combineLatest(observableB.onChange.startWith(Box.wrap(observableB.value))) @CaptureUnowned("combiner") { a: Box<A>, b: Box<B> -> boxWrap(combinerCopy(a.value, b.value)) }
                .skip(1)
        }
}

fun <T, B, C> ObservableProperty<T>.combine(
    other: ObservableProperty<B>,
    combiner: @Escaping() (T, B) -> C
): ObservableProperty<C> {
    return CombineObservableProperty(this, other, combiner)
}

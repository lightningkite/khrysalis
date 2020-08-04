package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.Escaping
import com.lightningkite.khrysalis.boxWrap
import io.reactivex.Observable

class TransformedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val read: @Escaping() (A) -> B
) : ObservableProperty<B>() {
    override val value: B
        get() {
            return read(basedOn.value)
        }
    override val onChange: Observable<Box<B>> = basedOn.onChange.map { it -> boxWrap(read(it.value)) }
}

@Deprecated("Use 'map' instead", ReplaceWith(
    "this.map(read)",
    "com.lightningkite.khrysalis.observables.map"
)
)
fun <T, B> ObservableProperty<T>.transformed(read: @Escaping() (T) -> B): ObservableProperty<B> {
    return TransformedObservableProperty<T, B>(this, read)
}

fun <T, B> ObservableProperty<T>.map(read: @Escaping() (T) -> B): ObservableProperty<B> {
    return TransformedObservableProperty<T, B>(this, read)
}

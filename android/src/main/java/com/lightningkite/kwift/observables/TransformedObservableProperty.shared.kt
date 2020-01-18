package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import com.lightningkite.kwift.escaping
import io.reactivex.Observable

class TransformedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val read: @escaping() (A) -> B
) : ObservableProperty<B>() {
    override val value: B
        get() {
            return read(basedOn.value)
        }
    @Suppress("UNCHECKED_CAST")
    override val onChange: Observable<Optional<B>> = basedOn.onChange.map { it -> Optional.wrap(read(it.value as A)) }
}

@Deprecated("Use 'map' instead", ReplaceWith(
    "this.map(read)",
    "com.lightningkite.kwift.observables.map"
)
)
fun <T, B> ObservableProperty<T>.transformed(read: @escaping() (T) -> B): ObservableProperty<B> {
    return TransformedObservableProperty<T, B>(this, read)
}

fun <T, B> ObservableProperty<T>.map(read: @escaping() (T) -> B): ObservableProperty<B> {
    return TransformedObservableProperty<T, B>(this, read)
}

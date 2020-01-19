package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.escaping
import io.reactivex.Observable

class TransformedMutableObservableProperty<A, B>(
    val basedOn: MutableObservableProperty<A>,
    val read: @escaping() (A) -> B,
    val write: @escaping() (B) -> A
) : MutableObservableProperty<B>() {
    override fun update() {
        basedOn.update()
    }

    override var value: B
        get() {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(value)
        }
    override val onChange: Observable<Box<B>> = basedOn.onChange.map { it -> boxWrap(read(it.value)) }
}


@Deprecated("Use 'map' instead", ReplaceWith(
    "this.map(read, write)",
    "com.lightningkite.kwift.observables.map"
)
)
fun <T, B> MutableObservableProperty<T>.transformed(
    read: @escaping() (T) -> B,
    write: @escaping() (B) -> T
): MutableObservableProperty<B> {
    return TransformedMutableObservableProperty<T, B>(this, read, write)
}

fun <T, B> MutableObservableProperty<T>.map(
    read: @escaping() (T) -> B,
    write: @escaping() (B) -> T
): MutableObservableProperty<B> {
    return TransformedMutableObservableProperty<T, B>(this, read, write)
}

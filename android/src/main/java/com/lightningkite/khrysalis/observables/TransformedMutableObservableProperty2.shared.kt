package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.Escaping
import com.lightningkite.khrysalis.boxWrap
import io.reactivex.Observable

class TransformedMutableObservableProperty2<A, B>(
    val basedOn: MutableObservableProperty<A>,
    val read: @Escaping() (A) -> B,
    val write: @Escaping() (A, B) -> A
) : MutableObservableProperty<B>() {
    override fun update() {
        basedOn.update()
    }

    override var value: B
        get() {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(basedOn.value, value)
        }
    override val onChange: Observable<Box<B>> = basedOn.onChange.map { it -> boxWrap(read(it.value)) }
}

fun <T, B> MutableObservableProperty<T>.mapWithExisting(
    read: @Escaping() (T) -> B,
    write: @Escaping() (T, B) -> T
): MutableObservableProperty<B> {
    return TransformedMutableObservableProperty2<T, B>(this, read, write)
}

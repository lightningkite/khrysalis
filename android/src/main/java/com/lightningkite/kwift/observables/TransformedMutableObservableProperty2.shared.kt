package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.escaping
import io.reactivex.Observable

class TransformedMutableObservableProperty2<A, B>(
    val basedOn: MutableObservableProperty<A>,
    val read: @escaping() (A) -> B,
    val write: @escaping() (A, B) -> A
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
    read: @escaping() (T) -> B,
    write: @escaping() (T, B) -> T
): MutableObservableProperty<B> {
    return TransformedMutableObservableProperty2<T, B>(this, read, write)
}

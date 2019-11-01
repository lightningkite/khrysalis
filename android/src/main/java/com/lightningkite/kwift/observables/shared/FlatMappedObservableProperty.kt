package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping
import java.io.Closeable

class FlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @escaping() (A) -> ObservableProperty<B>
) : ObservableProperty<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    override val onChange: Event<B>
        get() = FMOPEvent(this)

    class FMOPEvent<A, B>(val fmop: FlatMappedObservableProperty<A, B>): Event<B>() {
        override fun add(listener: (B) -> Boolean): Close {
            var current: Close = fmop.transformation(fmop.basedOn.value).onChange.add(listener)
            val closeA = fmop.basedOn.onChange.add {
                current.close()
                val new = fmop.transformation(fmop.basedOn.value)
                current = new.onChange.add(listener)
                listener(new.value)
                return@add false
            }
            return Close {
                current.close()
                closeA.close()
            }
        }
    }
}

fun <T, B> ObservableProperty<T>.flatMap(transformation: @escaping() (T) -> ObservableProperty<B>): FlatMappedObservableProperty<T, B> {
    return FlatMappedObservableProperty<T, B>(this, transformation)
}

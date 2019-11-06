package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping

class FlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @escaping() (A) -> ObservableProperty<B>
) : ObservableProperty<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    override val onChange: Event<B>
        get() = FMOPEvent(this)

    class FMOPEvent<A, B>(val fmop: FlatMappedObservableProperty<A, B>): Event<B>() {
        override fun add(listener: @escaping() (B) -> Boolean): Close {
            var current: Close = fmop.transformation(fmop.basedOn.value).onChange.add(listener = listener)
            val closeA = this.fmop.basedOn.onChange.add { it ->
                current.close()
                val new = this.fmop.transformation(this.fmop.basedOn.value)
                current = new.onChange.add(listener = listener)
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

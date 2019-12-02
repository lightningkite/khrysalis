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


class MutableFlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @escaping() (A) -> MutableObservableProperty<B>
) : MutableObservableProperty<B>() {
    override var value: B
        get() = transformation(basedOn.value).value
        set(value) {
            transformation(basedOn.value).value = value
        }
    override val onChange: Event<B>
        get() = FMOPEvent(this)

    override fun update() {
        transformation(basedOn.value).update()
    }

    class FMOPEvent<A, B>(val fmop: MutableFlatMappedObservableProperty<A, B>): Event<B>() {
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

fun <T, B> ObservableProperty<T>.flatMapMutable(transformation: @escaping() (T) -> MutableObservableProperty<B>): MutableFlatMappedObservableProperty<T, B> {
    return MutableFlatMappedObservableProperty<T, B>(this, transformation)
}

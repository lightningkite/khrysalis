package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import com.lightningkite.kwift.escaping
import io.reactivex.Observable

class FlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @escaping() (A) -> ObservableProperty<B>
) : ObservableProperty<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    @Suppress("UNCHECKED_CAST")
    override val onChange: Observable<Optional<B>>
        get() = basedOn.onChange.flatMap { it -> transformation(it.value as A).onChange }
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

    var lastProperty: MutableObservableProperty<B>? = null

    @Suppress("UNCHECKED_CAST")
    override val onChange: Observable<Optional<B>>
        get() = basedOn.onChange.flatMap { it ->
            val prop = transformation(it.value as A)
            this.lastProperty = prop
            return@flatMap prop.onChange
        }

    override fun update() {
        lastProperty?.update()
    }
}

fun <T, B> ObservableProperty<T>.flatMapMutable(transformation: @escaping() (T) -> MutableObservableProperty<B>): MutableFlatMappedObservableProperty<T, B> {
    return MutableFlatMappedObservableProperty<T, B>(this, transformation)
}

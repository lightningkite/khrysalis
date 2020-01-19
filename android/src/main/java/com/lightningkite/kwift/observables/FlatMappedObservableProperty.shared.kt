package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.swiftReturnType
import io.reactivex.Observable

class FlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @escaping() (A) -> ObservableProperty<B>
) : ObservableProperty<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    override val onChange: Observable<Box<B>>
        get() = basedOn.onChange.flatMap { it -> this.transformation(it.value).onChange }
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

    override val onChange: Observable<Box<B>>
        get() = basedOn.onChange.flatMap @swiftReturnType("Observable<Box<B>>") label@{ it: Box<A> ->
            val prop = this.transformation(it.value)
            this.lastProperty = prop
            return@label prop.onChange
        }

    override fun update() {
        lastProperty?.update()
    }
}

fun <T, B> ObservableProperty<T>.flatMapMutable(transformation: @escaping() (T) -> MutableObservableProperty<B>): MutableFlatMappedObservableProperty<T, B> {
    return MutableFlatMappedObservableProperty<T, B>(this, transformation)
}

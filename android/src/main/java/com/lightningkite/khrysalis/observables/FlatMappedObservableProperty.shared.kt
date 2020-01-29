package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.swiftReturnType
import io.reactivex.Observable

class FlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @escaping() (A) -> ObservableProperty<B>
) : ObservableProperty<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    override val onChange: Observable<Box<B>>
        get() = basedOn.observable.switchMap { it -> this.transformation(it.value).observable }.skip(1)
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
        get() = basedOn.observable.switchMap @swiftReturnType("Observable<Box<B>>") label@{ it: Box<A> ->
            val prop = this.transformation(it.value)
            this.lastProperty = prop
            return@label prop.observable
        }.skip(1)

    override fun update() {
        lastProperty?.update()
    }
}

fun <T, B> ObservableProperty<T>.flatMapMutable(transformation: @escaping() (T) -> MutableObservableProperty<B>): MutableFlatMappedObservableProperty<T, B> {
    return MutableFlatMappedObservableProperty<T, B>(this, transformation)
}

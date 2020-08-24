package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.Escaping
import com.lightningkite.khrysalis.WeakSelf
import io.reactivex.Observable

class FlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @Escaping() (A) -> ObservableProperty<B>
) : ObservableProperty<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    override val onChange: Observable<Box<B>>
        get() {
            val transformCopy = transformation
            return basedOn.observable.switchMap { it -> transformCopy(it.value).observable }.skip(1)
        }
}

fun <T, B> ObservableProperty<T>.switchMap(transformation: @Escaping() (T) -> ObservableProperty<B>): FlatMappedObservableProperty<T, B> {
    return FlatMappedObservableProperty<T, B>(this, transformation)
}

fun <T, B> ObservableProperty<T>.flatMap(transformation: @Escaping() (T) -> ObservableProperty<B>): FlatMappedObservableProperty<T, B> {
    return FlatMappedObservableProperty<T, B>(this, transformation)
}

fun <T: Any, B: Any> ObservableProperty<T?>.switchMapNotNull(transformation: @Escaping() (T) -> ObservableProperty<B?>): FlatMappedObservableProperty<T?, B?> {
    return FlatMappedObservableProperty<T?, B?>(this) { item ->
        if(item != null) return@FlatMappedObservableProperty transformation(item)
        else return@FlatMappedObservableProperty ConstantObservableProperty<B?>(null)
    }
}

fun <T: Any, B: Any> ObservableProperty<T?>.flatMapNotNull(transformation: @Escaping() (T) -> ObservableProperty<B?>): FlatMappedObservableProperty<T?, B?> {
    return FlatMappedObservableProperty<T?, B?>(this) { item ->
        if(item != null) return@FlatMappedObservableProperty transformation(item)
        else return@FlatMappedObservableProperty ConstantObservableProperty<B?>(null)
    }
}

class MutableFlatMappedObservableProperty<A, B>(
    val basedOn: ObservableProperty<A>,
    val transformation: @Escaping() (A) -> MutableObservableProperty<B>
) : MutableObservableProperty<B>() {
    override var value: B
        get() = transformation(basedOn.value).value
        set(value) {
            transformation(basedOn.value).value = value
        }

    var lastProperty: MutableObservableProperty<B>? = null

    override val onChange: Observable<Box<B>>
        get() {
            val transformCopy = transformation
            return basedOn.observable.switchMap @WeakSelf() { it: Box<A> ->
                val prop = transformCopy(it.value)
                this?.lastProperty = prop
                prop.observable
            }.skip(1)
        }

    override fun update() {
        lastProperty?.update()
    }
}

fun <T, B> ObservableProperty<T>.switchMapMutable(transformation: @Escaping() (T) -> MutableObservableProperty<B>): MutableFlatMappedObservableProperty<T, B> {
    return MutableFlatMappedObservableProperty<T, B>(this, transformation)
}

fun <T, B> ObservableProperty<T>.flatMapMutable(transformation: @Escaping() (T) -> MutableObservableProperty<B>): MutableFlatMappedObservableProperty<T, B> {
    return MutableFlatMappedObservableProperty<T, B>(this, transformation)
}

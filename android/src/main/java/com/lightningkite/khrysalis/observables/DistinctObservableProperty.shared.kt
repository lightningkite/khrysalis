package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.Escaping
import io.reactivex.Observable

class RxTransformationOnlyObservableProperty<T>(
    val basedOn: ObservableProperty<T>,
    val operator: @Escaping() (Observable<Box<T>>) -> Observable<Box<T>>
) : ObservableProperty<T>() {
    override val value: T
        get() = basedOn.value

    override val onChange: Observable<Box<T>> get() = operator(basedOn.onChange)
}

fun <T> ObservableProperty<T>.distinctUntilChanged(): ObservableProperty<T> = plusRx { it.startWith(Box.wrap(value)).distinctUntilChanged().skip(1) }

fun <T> ObservableProperty<T>.plusRx(operator: @Escaping() (Observable<Box<T>>) -> Observable<Box<T>>): ObservableProperty<T> {
    return RxTransformationOnlyObservableProperty<T>(this, operator)
}
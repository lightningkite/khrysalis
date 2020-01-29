package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.boxWrap
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class StandardObservableProperty<T>(
    var underlyingValue: T,
    override val onChange: Subject<Box<T>> = PublishSubject.create()
) : MutableObservableProperty<T>() {
    override var value: T
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            onChange.onNext(boxWrap(value))
        }

    override fun update() {
        onChange.onNext(boxWrap(value))
    }
}

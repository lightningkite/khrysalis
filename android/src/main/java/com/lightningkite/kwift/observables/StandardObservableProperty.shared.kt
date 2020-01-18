package com.lightningkite.kwift.observables

import com.lightningkite.kwift.Optional
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class StandardObservableProperty<T>(
    var underlyingValue: T,
    override val onChange: Subject<Optional<T>> = PublishSubject.create()
) : MutableObservableProperty<T>() {
    override var value: T
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            onChange.onNext(Optional.wrap(value))
        }

    override fun update() {
        onChange.onNext(Optional.wrap(value))
    }
}

@file:Suppress("UNCHECKED_CAST")

package com.lightningkite.kwift.observables

import com.lightningkite.kwift.*
import com.lightningkite.kwift.rx.addWeak
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

val <T> ObservableProperty<T>.observable: Observable<Optional<T>> get() = onChange.startWith(Optional.wrap(value))
val <T> ObservableProperty<T>.observableNN: Observable<T> get() = onChange.startWith(Optional.wrap(value)).map { it -> it.valueNN }
val <T> ObservableProperty<T>.onChangeNN: Observable<T> get() = onChange.map { it -> it.valueNN }

@discardableResult
@Deprecated("Use 'observable' directly instead", ReplaceWith("this.observable.addWeak(referenceA, listener)", "com.lightningkite.kwift.observables.observable"))
fun <A : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    listener: @escaping() (A, T) -> Unit
): Disposable = observable.addWeak(
    referenceA = referenceA,
    listener = { a, value ->
        listener(
            a,
            value.value as T
        )
    }
)

@discardableResult
@Deprecated("Use 'observable' directly instead", ReplaceWith("this.observable.addWeak(referenceA, referenceB, listener)", "com.lightningkite.kwift.observables.observable"))
fun <A : AnyObject, B : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    referenceB: B,
    listener: @escaping() (A, B, T) -> Unit
): Disposable = observable.addWeak(
    referenceA = referenceA,
    referenceB = referenceB,
    listener = { a, b, value ->
        listener(
            a,
            b,
            value.value as T
        )
    }
)

@discardableResult
@Deprecated("Use 'observable' directly instead", ReplaceWith("this.observable.addWeak(referenceA, referenceB, referenceC, listener)", "com.lightningkite.kwift.observables.observable"))
fun <A : AnyObject, B : AnyObject, C : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    referenceB: B,
    referenceC: C,
    listener: @escaping() (A, B, C, T) -> Unit
): Disposable = observable.addWeak(
    referenceA = referenceA,
    referenceB = referenceB,
    referenceC = referenceC,
    listener = { a, b, c, value ->
        listener(
            a,
            b,
            c,
            value.value as T
        )
    }
)

fun <E> includes(collection: MutableObservableProperty<Set<E>>, element: E): MutableObservableProperty<Boolean> {
    return collection.map { it ->
        it.contains(element)
    }.withWrite { it ->
        if (it) {
            collection.value = collection.value.plus(element)
        } else {
            collection.value = collection.value.minus(element)
        }
    }
}

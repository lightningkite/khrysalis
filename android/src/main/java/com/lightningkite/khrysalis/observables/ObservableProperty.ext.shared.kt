package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.rx.addWeak
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

val <T> ObservableProperty<T>.observable: Observable<Box<T>> get() = onChange.startWith(boxWrap(value))
val <T> ObservableProperty<T>.observableNN: Observable<T> get() = onChange.startWith(boxWrap(value)).map { it -> it.value }
val <T> ObservableProperty<T>.onChangeNN: Observable<T> get() = onChange.map { it -> it.value }

@CheckReturnValue
inline fun <T> ObservableProperty<T>.subscribeBy(
    noinline onError: @escaping() (Throwable) -> Unit = { it -> it.printStackTrace() },
    noinline onComplete: @escaping() () -> Unit = {},
    crossinline onNext: @escaping() (T) -> Unit = { it -> }
): Disposable = this.observable.subscribeBy(
    onError = onError,
    onComplete = onComplete,
    onNext = { boxed -> onNext(boxed.value) }
)

@Deprecated("Just use RX disposal stuff")
@discardableResult
fun <A : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    listener: @escaping() (A, T) -> Unit
): Disposable = observable.addWeak(
    referenceA = referenceA,
    listener = { a, value ->
        listener(
            a,
            value.value
        )
    }
)

@Deprecated("Just use RX disposal stuff")
@discardableResult
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
            value.value
        )
    }
)

@Deprecated("Just use RX disposal stuff")
@discardableResult
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
            value.value
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

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

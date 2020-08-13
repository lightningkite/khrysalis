package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.rx.addWeak
import com.lightningkite.khrysalis.rx.forever
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

val <T> ObservableProperty<T>.observable: Observable<Box<T>> get() = onChange.startWith { it.onNext(boxWrap(value)) }
val <T> ObservableProperty<T>.observableNN: Observable<T> get() = onChange.startWith { it.onNext(boxWrap(value)) }.map { it -> it.value }
val <T> ObservableProperty<T>.onChangeNN: Observable<T> get() = onChange.map { it -> it.value }

@CheckReturnValue
inline fun <T> ObservableProperty<T>.subscribeBy(
    noinline onError: @Escaping() (Throwable) -> Unit = { it -> it.printStackTrace() },
    noinline onComplete: @Escaping() () -> Unit = {},
    crossinline onNext: @Escaping() (T) -> Unit = { it -> }
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

fun ObservableProperty<Boolean>.whileActive(action: @Escaping() () -> Disposable): Disposable {
    var current: Disposable? = null
    return this.subscribeBy {
        if (it) {
            if (current == null) {
                current = action()
            }
        } else {
            current?.dispose()
            current = null
        }
    }
}
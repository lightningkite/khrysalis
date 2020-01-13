package com.lightningkite.kwift.async

import com.lightningkite.kwift.escaping

typealias DRF<T> = DelayedResultFunction<T>
inline class DelayedResultFunction<T>(val value: @escaping() (@escaping() (T) -> Unit) -> Unit) {
    fun invoke(callback: @escaping() (T) -> Unit) {
        value(callback)
    }
}

fun <T> immediate(action: @escaping() ()->T): DelayedResultFunction<T> {
    return DelayedResultFunction { onResult -> onResult(action()) }
}
fun <T> drfStart(value: T): DelayedResultFunction<T> = DelayedResultFunction { onResult -> onResult(value) }

fun <T, B> DelayedResultFunction<T>.then(next: (T) -> DelayedResultFunction<B>): DelayedResultFunction<B> {
    val first = this
    return DelayedResultFunction { onResult ->
        first.invoke { input ->
            next(input).invoke(onResult)
        }
    }
}
fun <T, B> DelayedResultFunction<T>.thenImmediate(next: (T) -> B): DelayedResultFunction<B> {
    val first = this
    return DelayedResultFunction<B> { onResult ->
        first.invoke { input ->
            onResult(next(input))
        }
    }
}

fun test() {
    drfStart(2)
        .then { a -> immediate { 4 + a } }
        .thenImmediate { a -> 4 + a }
        .then { a -> DelayedResultFunction<Double> { onResult -> onResult(a.toDouble() + 4.0) } }
        .invoke {
            println(it)
        }
}

package com.lightningkite.kwift.async

import com.lightningkite.kwift.Failable
import com.lightningkite.kwift.escaping

typealias DRF<T> = DelayedResultFunction<T>
inline class DelayedResultFunction<T>(val value: @escaping() (@escaping() (Failable<T>) -> Unit) -> Unit) {
    fun invoke(callback: @escaping() (Failable<T>) -> Unit) {
        value(callback)
    }
}

fun <T> immediate(action: @escaping() ()->T): DelayedResultFunction<T> {
    return DelayedResultFunction { onResult -> onResult(Failable(result = action())) }
}
fun <T> drfStart(value: T): DelayedResultFunction<T> = DelayedResultFunction { onResult -> onResult(Failable(result = value)) }

fun <T, B> DelayedResultFunction<T>.then(next: (T) -> DelayedResultFunction<B>): DelayedResultFunction<B> {
    val first = this
    return DelayedResultFunction { onResult ->
        first.invoke { input ->
            input.issue?.let {
                onResult(Failable(issue = it))
            } ?: run {
                next(input.result as T).invoke(callback = onResult)
            }
        }
    }
}
fun <T, B> DelayedResultFunction<T>.thenImmediate(next: (T) -> B): DelayedResultFunction<B> {
    val first = this
    return DelayedResultFunction<B> { onResult ->
        first.invoke { input ->
            input.issue?.let {
                onResult(Failable(issue = it))
            } ?: run {
                try {
                    onResult(Failable(result = next(input.result as T)))
                } catch(e:Exception) {
                    onResult(Failable(issue = e.message))
                }
            }
        }
    }
}

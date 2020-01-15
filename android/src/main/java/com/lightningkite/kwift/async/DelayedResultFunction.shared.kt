package com.lightningkite.kwift.async

import com.lightningkite.kwift.Failable
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.observables.Close
import java.io.Closeable

typealias DRF<T> = DelayedResultFunction<T>

inline class DelayedResultFunction<T>(val value: @escaping() (@escaping() (Failable<T>) -> Unit) -> Closeable) {
    fun invoke(callback: @escaping() (Failable<T>) -> Unit): Closeable {
        return value(callback)
    }
}

fun <T> immediate(action: @escaping() () -> T): DelayedResultFunction<T> {
    return DelayedResultFunction { onResult ->
        onResult(Failable(result = action()))
        return@DelayedResultFunction Close {}
    }
}

fun <T> drfStart(value: T): DelayedResultFunction<T> = DelayedResultFunction { onResult ->
    onResult(Failable(result = value))
    return@DelayedResultFunction Close {}
}

fun <T, B> DelayedResultFunction<T>.then(next: @escaping() (T) -> DelayedResultFunction<B>): DelayedResultFunction<B> {
    val first = this
    return DelayedResultFunction<B> { onResult ->
        var closed = false
        var secondCloseable: Closeable? = null
        var firstCloseable: Closeable? = null
        firstCloseable = first.invoke { input ->
            firstCloseable = null
            input.issue?.let {
                onResult(Failable<B>(issue = it))
            } ?: run {
                if(closed) onResult(Failable<B>(issue = "Closed"))
                else {
                    secondCloseable = next(input.result as T).invoke(callback = onResult)
                }
            }
        }
        return@DelayedResultFunction Close {
            closed = true
            firstCloseable?.close()
            secondCloseable?.close()
        }
    }
}

fun <T, B> DelayedResultFunction<T>.thenImmediate(next: @escaping() (T) -> B): DelayedResultFunction<B> {
    val first = this
    return DelayedResultFunction<B> { onResult ->
        return@DelayedResultFunction first.invoke { input ->
            input.issue?.let {
                onResult(Failable(issue = it))
            } ?: run {
                onResult(Failable(result = next(input.result as T)))
            }
        }
    }
}

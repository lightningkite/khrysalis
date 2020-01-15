package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

class FlatMappedEvent<A, B>(val source: Event<A>, val next: @escaping() (A)->Event<B>): Event<B>() {
    override fun add(listener: @escaping() (B) -> Boolean): Close {
        var end = false
        var closeableB: Closeable? = null
        val closeableA = source.add { it ->
            val nextEvent = this.next(it)
            closeableB?.close()
            closeableB = nextEvent.add { value ->
                val result = listener(value)
                end = result
                return@add result
            }
            return@add end
        }
        return Close {
            closeableB?.close()
            closeableA.close()
        }
    }
}

fun <T, B> Event<T>.flatMap(transformation: @escaping() (T) -> Event<B>): Event<B> {
    return FlatMappedEvent<T, B>(this, transformation)
}

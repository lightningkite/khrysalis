package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.escaping

class TransformedEvent<A, B>(
    val basedOn: Event<A>,
    val transformation: @escaping() (A) -> B
) : Event<B>() {
    override fun add(listener: @escaping() (B) -> Boolean): Close {
        val transformation = this.transformation
        return basedOn.add { it ->
            listener(transformation(it))
        }
    }
}

fun <T, B> Event<T>.transformed(transformation: @escaping() (T) -> B): Event<B> {
    return TransformedEvent<T, B>(this, transformation)
}

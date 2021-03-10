package com.lightningkite.khrysalis.generic

import java.util.*
import kotlin.collections.HashMap

abstract class OpSet<T : OpSet.Op<Identifier, Item>, Identifier, Item> {
    val handlers: HashMap<Identifier, TreeSet<T>> = HashMap()
    abstract fun identify(item: Item): Identifier

    interface Op<Identifier, Item> : Comparable<Op<*, *>> {
        val identifier: Identifier
        val priority: Int get() = 0
        val condition: (Item)->Boolean get() = { true }
        override fun compareTo(other: Op<*, *>): Int = -this.priority.compareTo(other.priority)
    }

    operator fun plusAssign(handler: T) {
        handlers.getOrPut(handler.identifier){ TreeSet() }.add(handler)
    }

    fun sequence(item: Item): Sequence<T> {
        return handlers[identify(item)]
            ?.asSequence()
            ?.filter { it.condition(item) }
            ?: emptySequence()
    }

    operator fun get(item: Item, startAtPriority: Int = Int.MAX_VALUE): T? = sequence(item)
        .dropWhile { it.priority >= startAtPriority }
        .firstOrNull()
}

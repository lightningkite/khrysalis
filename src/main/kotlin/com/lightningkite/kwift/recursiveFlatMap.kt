package com.lightningkite.kwift

/**
 * Creates a recursive sequence, breadth first.
 */
fun <T> Sequence<T>.recursiveFlatMap(getter: (T) -> Sequence<T>) = object : Sequence<T> {
    override fun iterator(): Iterator<T> = this@recursiveFlatMap.iterator().recursiveFlatMap({ getter(it).iterator() })
}

/**
 * Creates a recursive iterator, breadth first.
 */
fun <T> Iterator<T>.recursiveFlatMap(getter: (T) -> Iterator<T>) = object : Iterator<T> {

    val toCheck = ArrayList<T>()
    var current: Iterator<T> = this@recursiveFlatMap

    override fun hasNext(): Boolean = current.hasNext()
    override fun next(): T {
        val item = current.next()

        toCheck.add(item)

        while (true) {
            if (current.hasNext()) {
                break
            } else if (toCheck.isNotEmpty()) {
                val newCheck = toCheck.removeAt(0)
                current = getter(newCheck)
            } else {
                break
            }
        }

        return item
    }
}

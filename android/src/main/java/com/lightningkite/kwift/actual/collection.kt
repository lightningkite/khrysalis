package com.lightningkite.kwift.actual

fun <T> List<T>.withoutIndex(index: Int): List<T> {
    return this.toMutableList().apply { removeAt(index) }
}

inline fun <T, K : Comparable<K>> MutableList<T>.binaryInsertBy(
    item: T,
    crossinline selector: (T) -> K?
) {
    val index = binarySearchBy(selector(item), selector = selector)
    if (index < 0) {
        add(
            index = -index - 1,
            element = item
        )
    } else {
        add(
            index = index,
            element = item
        )
    }
}

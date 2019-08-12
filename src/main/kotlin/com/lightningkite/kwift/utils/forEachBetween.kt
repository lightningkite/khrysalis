package com.lightningkite.kwift.utils

inline fun <T> Iterable<T>.forEachBetween(
    forItem: (T) -> Unit,
    between: () -> Unit
) {
    var hasDoneFirst = false
    forEach {
        if (hasDoneFirst) {
            between()
        } else {
            hasDoneFirst = true
        }
        forItem(it)
    }
}


inline fun <T> Sequence<T>.forEachBetween(
    forItem: (T) -> Unit,
    between: () -> Unit
) {
    var hasDoneFirst = false
    forEach {
        if (hasDoneFirst) {
            between()
        } else {
            hasDoneFirst = true
        }
        forItem(it)
    }
}

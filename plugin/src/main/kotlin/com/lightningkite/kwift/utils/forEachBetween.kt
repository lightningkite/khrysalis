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

inline fun <T> Iterable<T>.forEachBetweenIndexed(
    forItem: (Int, T) -> Unit,
    between: () -> Unit
) {
    var hasDoneFirst = false
    forEachIndexed { index, it ->
        if (hasDoneFirst) {
            between()
        } else {
            hasDoneFirst = true
        }
        forItem(index, it)
    }
}


inline fun <T> Sequence<T>.forEachBetweenIndexed(
    forItem: (Int, T) -> Unit,
    between: () -> Unit
) {
    var hasDoneFirst = false
    forEachIndexed { index, it ->
        if (hasDoneFirst) {
            between()
        } else {
            hasDoneFirst = true
        }
        forItem(index, it)
    }
}

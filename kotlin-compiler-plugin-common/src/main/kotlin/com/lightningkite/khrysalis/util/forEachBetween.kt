package com.lightningkite.khrysalis.util

import java.util.concurrent.*

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

fun <T> Collection<T>.forEachMultithreaded(action: (T)->Unit) {
    this.parallelStream().forEach(action)
}

fun <T> Sequence<T>.forEachMultithreaded(action: (T)->Unit) {
//    this.asStream().parallel().forEach(action)
    this.forEach(action)
}

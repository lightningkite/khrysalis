package com.lightningkite.kwift.actual

fun <T> List<T>.withoutIndex(index: Int): List<T> {
    return this.toMutableList().apply { removeAt(index) }
}

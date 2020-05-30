package com.lightningkite.khrysalis.flow

interface Mergable<T> {
    fun merge(other: T): T?
}
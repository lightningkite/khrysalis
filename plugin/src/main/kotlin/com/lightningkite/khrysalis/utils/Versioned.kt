package com.lightningkite.khrysalis.utils

data class Versioned<T>(
    val version: Int,
    val value: T
)

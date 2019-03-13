package com.lightningkite.kwift.utils

data class Versioned<T>(
    val version: Int,
    val value: T
)

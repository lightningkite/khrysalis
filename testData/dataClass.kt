package com.lightningkite.kwift.test

data class TestDataClass(
    val a: Double = 0.0,
    val b: String = "Hello!",
    val c: List<Int> = listOf<Int>()
): Serializable

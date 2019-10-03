package com.lightningkite.kwift.utils

operator fun <K, V: Any> Map<K, V>.get(vararg keys: K) = keys.asSequence().mapNotNull { this[it] }.firstOrNull()

fun <K, V: Any> Map<K, V>.getMany(vararg keys: K) = keys.asSequence().mapNotNull { this[it] }
fun <K, V: Any> Map<K, V>.getMany(keys: Sequence<K>) = keys.mapNotNull { this[it] }
fun <K, V: Any> Map<K, V>.getMany(keys: Iterable<K>) = keys.asSequence().mapNotNull { this[it] }

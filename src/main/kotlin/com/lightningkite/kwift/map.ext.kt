package com.lightningkite.kwift

operator fun <K, V: Any> Map<K, V>.get(vararg keys: K) = keys.asSequence().mapNotNull { this[it] }.firstOrNull()

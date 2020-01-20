package com.lightningkite.kwift.observables

import com.lightningkite.kwift.rx.addWeak
import org.junit.Assert.*
import org.junit.Test

class ObservablePropertiesTest {
    @Test
    fun transform() {
        val source = StandardObservableProperty(1)
        source.map { it + 1 }.observableNN.addWeak(this) { self, value ->
            println(value)
        }
        source.value = 2
        source.value = 4
        source.value = 5
        source.value = 6
    }
}

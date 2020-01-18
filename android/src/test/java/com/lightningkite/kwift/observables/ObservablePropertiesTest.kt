package com.lightningkite.kwift.observables

import org.junit.Assert.*
import org.junit.Test

class ObservablePropertiesTest {
    @Test fun transform(){
        val source = StandardObservableProperty(1)
        val mapped = source.map { it + 1 }
        mapped.observableNN.subscribe { println(it) }
        source.value = 2
        source.value = 4
        source.value = 5
        source.value = 6
    }
}

package com.lightningkite.kwift.observables

import com.lightningkite.kwift.rx.addWeak
import org.junit.Assert.*
import org.junit.Test

class ObservablePropertiesTest {
    @Test
    fun transform() {
        val source = StandardObservableProperty(1)
        var read = 0
        source.map { it + 1 }.observableNN.addWeak(this) { self, value ->
            println(value)
            read = value
        }
        assertEquals(1 + 1, read)
        source.value = 2
        assertEquals(2 + 1, read)
        source.value = 4
        assertEquals(4 + 1, read)
        source.value = 5
        assertEquals(5 + 1, read)
        source.value = 6
        assertEquals(6 + 1, read)
    }

    @Test fun merge(){
        val sourceA = StandardObservableProperty("walk")
        val sourceB = StandardObservableProperty("ing")
        var read = ""
        sourceA.combine(sourceB){ a, b -> a + b }.observableNN.addWeak(this) { self, value ->
            println(value)
            read = value
        }
        assertEquals("walking", read)
        sourceA.value = "jump"
        assertEquals("jumping", read)
        sourceB.value = "er"
        assertEquals("jumper", read)
        sourceA.value = "walk"
        assertEquals("walker", read)
    }

    @Test fun nullStuff(){
        val sourceA = StandardObservableProperty<String?>(null)
        val sourceB = StandardObservableProperty<String?>(null)
        var read = ""
        sourceA.combine(sourceB){ a, b -> a + b }.observableNN.addWeak(this) { self, value ->
            println(value)
            read = value
        }
        assertEquals("nullnull", read)
        sourceA.value = "walk"
        assertEquals("walknull", read)
        sourceB.value = "ing"
        assertEquals("walking", read)
        sourceA.value = "jump"
        assertEquals("jumping", read)
        sourceB.value = "er"
        assertEquals("jumper", read)
        sourceA.value = "walk"
        assertEquals("walker", read)
    }
}

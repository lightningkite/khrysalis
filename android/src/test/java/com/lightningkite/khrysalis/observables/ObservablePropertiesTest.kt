package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.rx.addWeak
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
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

    @Test fun flatMap(){
        val source = StandardObservableProperty<MutableObservableProperty<String>>(StandardObservableProperty("a"))
        var read = ""
        source.flatMap { it }.observableNN.addWeak(this) { self, value ->
            println(value)
            read = value
        }
        assertEquals("a", read)
        source.value.value = "b"
        assertEquals("b", read)
        source.value.value = "c"
        val old = source.value
        assertEquals("c", read)
        source.value = StandardObservableProperty("d")
        assertEquals("d", read)
        source.value.value = "e"
        assertEquals("e", read)
        old.value = "wrong"
        assertEquals("e", read)
    }
}

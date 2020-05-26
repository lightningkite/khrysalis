package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.rx.addWeak
import com.lightningkite.khrysalis.rx.forever
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
        val r = source.map { it + 1 }
        r.observableNN.subscribeBy { value ->
            println(value)
            assertEquals(value, r.value)
            read = value
        }.forever()
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
        val r = sourceA.combine(sourceB){ a, b -> a + b }
        r.observableNN.subscribeBy { value ->
            println(value)
            assertEquals(value, r.value)
            read = value
        }.forever()
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
        val r = sourceA.combine(sourceB){ a, b -> a + b }
        r.observableNN.subscribeBy { value ->
            println(value)
            assertEquals(value, r.value)
            read = value
        }.forever()
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
        val r = source.flatMap { it }
        r.observableNN.subscribeBy { value ->
            println(value)
            assertEquals(value, r.value)
            read = value
        }.forever()
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

    @Test fun flatMapMap(){
        val source = StandardObservableProperty<MutableObservableProperty<String>>(StandardObservableProperty("a"))
        var read = ""
        val r = source.flatMap { it.map { it + "x" } }
        r.observableNN.subscribeBy { value ->
            println(value)
            assertEquals(value, r.value)
            read = value
        }.forever()
        assertEquals("ax", read)
        source.value.value = "b"
        assertEquals("bx", read)
        source.value.value = "c"
        val old = source.value
        assertEquals("cx", read)
        source.value = StandardObservableProperty("d")
        assertEquals("dx", read)
        source.value.value = "e"
        assertEquals("ex", read)
        old.value = "wrong"
        assertEquals("ex", read)
    }

    @Test fun toObservableProperty(){
        val subject = PublishSubject.create<Int>()
        var read = -1
        val r = subject.asObservableProperty(0)
        r.observableNN.subscribeBy { value ->
            println(value)
            assertEquals(value, r.value)
            read = value
        }.forever()
        subject.onNext(1)
        assertEquals(1, read)
        assertEquals(1, r.value)
        subject.onNext(2)
        assertEquals(2, read)
        assertEquals(2, r.value)
    }
}

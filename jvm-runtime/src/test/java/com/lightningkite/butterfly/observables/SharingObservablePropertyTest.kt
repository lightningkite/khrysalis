package com.lightningkite.butterfly.observables

import com.lightningkite.rxkotlinproperty.map
import com.lightningkite.rxkotlinproperty.share
import com.lightningkite.rxkotlinproperty.subscribeBy
import org.junit.Assert.*
import org.junit.Test

class SharingObservablePropertyTest {
    @Test fun calculationsCount(){
        var calculations = 0
        val source = StandardObservableProperty<Int>(0)
        val calculated = source.map {
            println("Calculating...")
            calculations += 1
            it + 1
        }
        val shared = calculated.share()

        println("isListening: ${shared.isListening}")
        val subA = shared.subscribeBy { println("A: $it") }
        println("isListening: ${shared.isListening}")
        println("direct: ${shared.value}")
        val subB = shared.subscribeBy { println("B: $it") }
        println("isListening: ${shared.isListening}")
        println("direct: ${shared.value}")
        val subC = shared.subscribeBy { println("C: $it") }
        println("isListening: ${shared.isListening}")
        println("direct: ${shared.value}")

        source.value = 5
        println("direct: ${shared.value}")

        subA.dispose()
        println("isListening: ${shared.isListening}")
        subB.dispose()
        println("isListening: ${shared.isListening}")
        subC.dispose()
        println("isListening: ${shared.isListening}")

        assertEquals(3, calculations)
    }
    @Test fun calculationsCountStartAsListening(){
        var calculations = 0
        val source = StandardObservableProperty<Int>(0)
        val calculated = source.map {
            println("Calculating...")
            calculations += 1
            it + 1
        }
        val shared = calculated.share(startAsListening = true)

        println("isListening: ${shared.isListening}")
        val subA = shared.subscribeBy { println("A: $it") }
        println("isListening: ${shared.isListening}")
        println("direct: ${shared.value}")
        val subB = shared.subscribeBy { println("B: $it") }
        println("isListening: ${shared.isListening}")
        println("direct: ${shared.value}")
        val subC = shared.subscribeBy { println("C: $it") }
        println("isListening: ${shared.isListening}")
        println("direct: ${shared.value}")

        source.value = 5
        println("direct: ${shared.value}")

        subA.dispose()
        println("isListening: ${shared.isListening}")
        subB.dispose()
        println("isListening: ${shared.isListening}")
        subC.dispose()
        println("isListening: ${shared.isListening}")

        assertEquals(2, calculations)
    }

    @Test fun accurateWithoutSubscription(){
        var calculations = 0
        val source = StandardObservableProperty<Int>(0)
        val calculated = source.map {
            println("Calculating...")
            calculations += 1
            it + 1
        }
        val shared = calculated.share()

        source.value = 4
        assertEquals(5, shared.value)
    }

    @Test fun accurateWithSubscription(){
        var calculations = 0
        val source = StandardObservableProperty<Int>(0)
        val calculated = source.map {
            println("Calculating...")
            calculations += 1
            it + 1
        }
        val shared = calculated.share()
        val sub = shared.subscribeBy { println("Got $it") }

        source.value = 4
        assertEquals(5, shared.value)
    }
}
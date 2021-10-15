package com.lightningkite.butterfly.observables

import com.lightningkite.butterfly.Box
import com.lightningkite.rxkotlinproperty.distinctUntilChanged
import com.lightningkite.rxkotlinproperty.subscribeBy
import org.junit.Assert.*
import org.junit.Test

class RxTransformationOnlyObservablePropertyTest {
    @Test fun distinct(){
        val source = StandardObservableProperty(0)
        var lastValue: Box<Int>? = null
        val mapped = source.distinctUntilChanged()
        var hits = 0
        val sub = mapped.subscribeBy {
            hits++
            println(it)
        }
        source.value = 0
        source.value = 0
        source.value = 1
        source.value = 2
        source.value = 2
        source.value = 1
        assertEquals(4, hits)
    }
}
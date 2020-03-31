package com.test

import com.lightningkite.khrysalis.*

class Thing(input: Any) {
    @unowned val thing: Any = input
    val lambda: ()->Unit get() = @unownedSelf() {
        println("Hello!")
    }
}

class DummyObject(){}

fun main(){
    Thing(DummyObject()).lambda()
}

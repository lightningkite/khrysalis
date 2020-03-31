package com.test

import com.lightningkite.khrysalis.*

class HasLambda(val action: @escaping() ()->Unit = {}) {
    fun invoke() {
        action()
    }
}

var globalThing: ()->Unit = {}
fun doThing(action: @escaping() () -> Unit) {
    globalThing = action
}

fun main(){
    HasLambda {
        println("Hello world!")
    }.invoke()
    doThing {
        println("Hello world 2!")
    }
    globalThing()
}

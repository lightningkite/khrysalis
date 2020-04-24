package com.test

object Singleton {
    val x: Int = 0
    fun doThing(argA: String, argB: Int = 3){
        println("Hello World!")
    }
}

fun main(){
    Singleton.doThing("asdf", 3)
}

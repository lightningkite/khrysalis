package com.test

data class Thing(
    val value: Int = 0,
    val sub: Thing? = null
) {
    fun test()
}

fun main(){
    val thing = Thing(0, Thing(1, Thing(2, null)))
    println(thing.sub?.sub?.value)
    thing?.test()
    thing?.sub?.test()
}

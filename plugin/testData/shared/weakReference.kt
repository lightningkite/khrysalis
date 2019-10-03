package com.test

fun testWeakRef(){
    val x: Int = 0
    val weakX: Int? by weak(x)
    println(weakX)
}

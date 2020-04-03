package com.test

fun main(){
    val a: Int = 32 + 48
    val b: Byte = 12
    val c: Short = 33
    val d: Long = 5489283492L
    val e = a + b.toInt() + c.toInt()
    val f = d + e.toLong()
    val g = 432.12
    val h: Float = 432.12f
    val i = g + h.toDouble()
    println("Success!")
}

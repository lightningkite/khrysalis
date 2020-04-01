package com.test

typealias MyInteger = Int

infix fun Int.doubleAdd(other: Int): Int {
    return this + other + other
}

val Int.half: Int get() = this / 2

val MyInteger.double: Int get() = this * 2

operator fun String.div(other: String): String {
    return this + "/" + other
}

fun main(){
    println(3.toInt() doubleAdd 5)
    println(18.toInt().half)
    println(18.toInt().double)
    println("a" / "b")
}

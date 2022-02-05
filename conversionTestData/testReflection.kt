@file:SharedCode
package com.test.reflection

import com.lightningkite.khrysalis.*
import kotlin.reflect.KProperty1

data class Test(var number: Int)

fun topLevelFunction(number: Int): Int {
    return number + 2
}

fun main() {
    val instance = Test(1)
    val reflective = Test::number
    println(reflective.get(instance))
    reflective.set(instance, 4)
    println(reflective.get(instance))
    val someFunc: (Int)->Int = ::topLevelFunction
    println(someFunc(3))
    val someFunc2 = ::topLevelFunction
    println(someFunc2(3))
}
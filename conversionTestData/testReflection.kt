@file:SharedCode
package com.test.reflection

import com.lightningkite.khrysalis.*
import kotlin.reflect.KProperty1

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class DatabaseModel

@DatabaseModel
data class Test(
    val number: Int
)
data class Normal(
    var number: Int
)

//fun topLevelFunction(number: Int): Int {
//    return number + 2
//}

fun main() {
    val instance = Test(1)
    val reflective = Test::number
    println(reflective.get(instance))
    val instance2 = Normal(1)
    val reflective2 = Normal::number
    println(reflective2.get(instance2))
    reflective2.set(instance2, 4)
    println(reflective2.get(instance2))
//    val someFunc: (Int)->Int = ::topLevelFunction
//    println(someFunc(3))
//    val someFunc2 = ::topLevelFunction
//    println(someFunc2(3))
}
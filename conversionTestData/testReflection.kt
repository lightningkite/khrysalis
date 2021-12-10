@file:SharedCode
package com.test.reflection

import com.lightningkite.khrysalis.*
import kotlin.reflect.KProperty1

data class Test(var number: Int)

fun main() {
    val instance = Test(1)
    val reflective = Test::number
    println(reflective.get(instance))
    reflective.set(instance, 4)
    println(reflective.get(instance))
}
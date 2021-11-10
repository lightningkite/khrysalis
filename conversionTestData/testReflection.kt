@file:SharedCode
package com.test.reflection

import com.lightningkite.butterfly.*

data class Test(val number: Int)

fun main() {
    val instance = Test(1)
    println(Test::number)
}
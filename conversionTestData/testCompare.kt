@file:SharedCode

package com.test.compare

import com.lightningkite.khrysalis.*

fun <T: Comparable<T>> altGt(left: T, right: T): Boolean = left > right
fun <T: Comparable<T>> altGt2(left: T, right: T): Boolean = left.compareTo(right) > 0

fun main(vararg args: String) {
    val list = listOf(1, 2, 3, 4, 5)
    list.sortedWith(compareBy { -it }).forEach {
        println(it)
    }
    println(altGt(1, 2))
    println(altGt2(1, 2))

}
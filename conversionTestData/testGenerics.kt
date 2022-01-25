@file:SharedCode
package com.test.generics

import com.lightningkite.khrysalis.*

class GenericTest<A, B: Comparable<B>, C, D>() {

}

fun GenericTest<*, *, *, *>.testA() {}
fun GenericTest<Int, Int, Int, Int>.testB() {}
fun <A> GenericTest<A, *, *, *>.testC() {}
fun <B: Comparable<B>> GenericTest<*, B, *, *>.testD() {}
fun <B: String> GenericTest<*, B, *, *>.testE() {}
fun <A, B: Comparable<B>, C, D> GenericTest<A, B, C, D>.testF() {}
fun <A, B: Comparable<B>, C, D> GenericTest<List<A>, B, C, D>.testG() {}

fun main() {
    println("Hello")
}
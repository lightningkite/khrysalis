@file:SharedCode
package com.test.generics

import com.lightningkite.khrysalis.*

class GenericTest<First, Second: Comparable<Second>, Third, Fourth>() {

}

open class SampleInput {}

fun GenericTest<*, *, *, *>.testA() {}
fun GenericTest<Int, Int, Int, Int>.testB() {}
fun <A: IsEquatable> GenericTest<A, *, *, *>.testC() {}
fun <B: Comparable<B>> GenericTest<*, B, *, *>.testD() {}
fun <B: String> GenericTest<*, B, *, *>.testE() {}
fun <A, B: Comparable<B>, C, D> GenericTest<A, B, C, D>.testF(): A { fatalError() }
fun <A: IsEquatable, B: Comparable<B>, C, D> GenericTest<List<A>, B, C, D>.testG() {}
operator fun <A: IsEquatable, B: Comparable<B>, C, D> GenericTest<List<A>, B, C, D>.get(key: String) {}
fun <A: SampleInput, B: Comparable<B>, C, D> GenericTest<A, B, C, D>.testH(f: A) {}

class Box<T>(val value: T) {
    fun <B> map(mapper: (T)->B): Box<B> = Box(mapper(value))
}

fun <T> List<T>.extensionOnList(): T? = this.firstOrNull()

fun starTyped(testFunction: ()->Box<*>) {}

fun main() {
    println("Hello")
    Box(32).map { it.toString() }
    starTyped { Box(32) }
    GenericTest<List<Int>, Int, Int, Int>().testG()
    GenericTest<List<Int>, Int, Int, Int>()["asdf"]
}
@file:SharedCode
package com.test.castrule

import com.lightningkite.khrysalis.*

interface TypeB { val x: Int }
data class TypeA(override val x: Int): TypeB

fun <T> callOther(item: T, func: (T)->Unit) {
    func(item)
}
fun usesB(b: TypeB) {}
fun usesA(a: TypeA) {}
fun starReplace(a: TypeA, b: TypeB) {}

fun main(){
    val x = TypeA(42)
    println(x.x)
    usesA(x)
    usesB(x)
    val y = TypeA::x.get(x)
    val z = TypeB::x.get(x)
    callOther(x) { y: TypeB -> println(y.x) }
    callOther<TypeB>(x) { y: TypeB -> println(y.x) }
    starReplace(x, x)
    println("Success")
}
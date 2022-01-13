@file:SharedCode
package com.test.castrule

import com.lightningkite.khrysalis.*

interface TypeB { val x: Int }
data class TypeA(override val x: Int): TypeB

fun usesB(b: TypeB) {}
fun usesA(a: TypeA) {}

fun main(){
    val x = TypeA(42)
    usesA(x)
    usesB(x)
    val y = TypeA::x.get(x)
    val z = TypeB::x.get(x)
    println("Success")
}
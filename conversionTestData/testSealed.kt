@file:SharedCode

package com.test.sealed

import com.lightningkite.khrysalis.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.builtins.*

sealed class Sealed {
    object A: Sealed() {
        override fun test() {
            println("A")
        }
    }
    class B(val x: Int): Sealed() {
        override fun test() {
            println("B has $x")
        }
    }
    abstract fun test()
}

fun main(vararg args: String) {
    val x: Sealed = Sealed.B(1)
    val y: Sealed = Sealed.A
    x.test()
    y.test()
}
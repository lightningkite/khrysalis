@file:SharedCode
package com.test.cursedclasses

import com.lightningkite.khrysalis.*

interface A {
    class B(val c: C = C()) {
        class C {
            companion object {
                val x: Int = 0
            }
            val d: D = D()
            class D() {
                constructor(jank: Int):this(){
                    println(jank)
                }
                interface E {
                    fun test() = println("Hi!")
                }
            }
        }
    }
}

interface F {
    class G {
    }
    object H {
        val x: Int = 0
        val y: G = G()
    }
}

fun main(){
    println("Clearly, we hate ourselves.")
    println(F.H.x)
    val other = F.G()

    A.B()
    A.B.C()
    A.B.C.D()
    A.B.C.D(4)
}
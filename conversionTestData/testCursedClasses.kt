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

class I {
    fun takeThing(j: I.J, j2: J) {
        I.J()
        J()
    }
    class J {

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

    I()
    I.J()
}
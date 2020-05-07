package com.test.cursedclasses

class A {
    class B {
        class C {
            class D {
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
    }
}

fun main(){
    println("Clearly, we hate ourselves.")
    println(F.H.x)
    val thing = object: A.B.C.D.E {
        val hello = "Hello World!"
    }
    val other = F.G()

    A()
    A.B()
    A.B.C()
    A.B.C.D()
}
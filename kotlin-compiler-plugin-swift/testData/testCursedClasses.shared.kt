package com.test

private class CursedA {
    class B {
        companion object {
            val x: Int = 0
        }
        fun otherTest(){
            println(x)
            println(B.x)
        }
        class C<T> {
            companion object {
                val x: Int = 0
                fun test(){
                    println(x)
                }
            }
            val d: D = D()
            val t: T? = null
            fun otherTest(){
                println(x)
                println(C.x)
            }
            class D() {
                constructor(jank: Int):this(){
                    println(jank)
                }
                interface E {
                    private fun test() = println("Hi!")
                }
                class JK(){

                }
            }
        }
    }
}

private interface CursedF {
    class G {
    }
    object H {
        val x: Int = 0
        val y: G = G()
    }
}

private fun cursedmain(){
    println("Clearly, we hate ourselves.")
    println(CursedF.H.x)
    println(CursedA.B.C.x)
    println(CursedA.B.x)
    val thing = object: CursedA.B.C.D.E {
        val hello = "Hello World!"
    }
    val other = CursedF.G()

    CursedA()
    CursedA.B()
    CursedA.B.C<Int>()
    CursedA.B.C.D()
    CursedA.B.C.D(4)
    CursedA.B.C.D.JK()
    println(CursedA.B.C)
    println(CursedA.B)
}
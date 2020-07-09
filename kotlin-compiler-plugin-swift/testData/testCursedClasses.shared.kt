package com.test

private class CursedA {
    class B {
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
                    private fun test() = println("Hi!")
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
    val thing = object: CursedA.B.C.D.E {
        val hello = "Hello World!"
    }
    val other = CursedF.G()

    CursedA()
    CursedA.B()
    CursedA.B.C()
    CursedA.B.C.D()
    CursedA.B.C.D(4)
}
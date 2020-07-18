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
        open class C<T> {
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

private class CursedL<T>(val x: Int = 0): CursedA.B.C<T>() {
    var y: Int = x
    val z: Int = y
    val ohno: Int get() = 2
    val a: Int = ohno
    val b: Int = 2.plus(ohno.plus(3))
    val c: Int = 2.plus(this.ohno.plus(3))
    val d2: CursedA.B.C.D = d
    val d3 = d
    val selfCapture = { this.x }
    val selfCapture2 = { x }
    companion object {
        val x: Int = 0
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
    println(CursedL.x)
}
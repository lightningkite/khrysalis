package com.test.classes

class GenericThing<T: Comparable<T>>(val item: T) {
    inner class SomeThing(val comparableThing: T){
        fun test() = comparableThing > item
        fun explicitTest() = this.comparableThing > this@GenericThing.item
    }
}

fun testInner(){

    val gen = GenericThing<Int>(4)
    val genSub: GenericThing<Int>.SomeThing = gen.SomeThing(5)
}
package com.test.types

typealias MyInteger = Int

class Thing()

typealias MyThing = Thing
typealias MyList<T> = List<T>
typealias ListOfThings = MyList<MyThing>

fun main(){
    val x: Int = 0
    val y: MyInteger = 0
    val stuff: ListOfThings = listOf(MyThing(), Thing())
    val otherList: List<Int> = listOf(1, 2, 3)
    println("Success")
}

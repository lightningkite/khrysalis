package com.test.types

typealias MyInteger = Int

class Thing()

typealias MyThing = Thing
typealias MyList<T> = List<T>
typealias ListOfThings = MyList<MyThing>

fun main(){
    val stuff: ListOfThings = listOf(MyThing(), Thing())
    println("Success")
}

package com.test.types

import com.test.classes.TestInterface
import com.test.classes.Weird
import com.lightningkite.khrysalis.*

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
    val nullabilityTest: Int? = null
    val nullabilityTest2: ListOfThings? = null
    println("Success")

    val ugh = Weird(2)
    val unknownThing: Any? = ugh

    if(unknownThing is TestInterface){
        println("Hello!")
    }
    println(unknownThing as? TestInterface)

    if(unknownThing is Weird){
        println("Hello!")
    }
    println(unknownThing as? Weird)

    if(unknownThing is Int){
        println("Hello!")
    }
    println(unknownThing as? Int)

    val thingA: AnyObject = unknownThing
}

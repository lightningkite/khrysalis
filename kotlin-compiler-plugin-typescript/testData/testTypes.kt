@file:SharedCode
package com.test.types

import com.lightningkite.butterfly.*

interface TestInterface {
    val interfaceValue: String get() = "Default"
    fun interfaceFunction(): String = "Default"
}


open class Weird(a: Int = 0, b: String, val c: Double, var d: Long): TestInterface {
    val e: Int = 0
    var f: String
    init {
        f = "asdf"
    }

    constructor(both: Int): this(both, both.toString(), both.toDouble(), both.toLong()) {
        f = "Something else"
    }

    override val interfaceValue: String
        get() = f

    open fun test(){
        println("Hi!")
    }

    override fun toString(): String {
        return "Weird $f"
    }
}

typealias MyInteger = Int

class Thing()

typealias MyThing = Thing
typealias MyList<T> = List<T>
typealias ListOfThings = MyList<MyThing>

fun notNullThing(thing: Any){
    println(thing.toString())
}

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
    var unknownThingMut: Any? = unknownThing
    if(x < 0) {
        unknownThingMut = 2
    }

    if(unknownThing is TestInterface){
        println("Hello!")
    }
    (unknownThing as? TestInterface)?.let { println(it.toString() )}

    if(unknownThing is Weird){
        println("Hello!")
    }
    (unknownThing as? Weird)?.let { println(it.toString() )}

    if(unknownThing is Int){
        println("Hello!")
    }
    (unknownThing as? Int)?.let { println(it.toString() )}

    if(unknownThingMut is String){
        println(unknownThingMut + "asdf")
    }
    if(unknownThingMut != null){
        notNullThing(unknownThingMut)
    }

    val thingA: AnyObject = unknownThing!!

    val a = 23L
    val b = 23f
    val c = 23
    val d = 23.0

    val unit: Unit = Unit
    val returnsUnit: ()->Unit = label@{
        return@label Unit
    }
}

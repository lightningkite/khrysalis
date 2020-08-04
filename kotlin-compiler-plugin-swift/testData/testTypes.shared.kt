package com.test

import com.lightningkite.khrysalis.*

private typealias TypesMyInt = Int

private class TypesThing()

private typealias MyThing = TypesThing
private typealias MyList<T> = List<T>
private typealias ListOfThings = MyList<MyThing>

private class TypesConstructorTest(
){
    val x = HashMap<String, Long>()

}

private fun typesMain(){
    val x: Int = 0
    val y: TypesMyInt = 0
    val stuff: ListOfThings = listOf(MyThing(), TypesThing())
    val otherList: List<Int> = listOf(1, 2, 3)
    val nullabilityTest: Int? = null
    val nullabilityTest2: ListOfThings? = null
    println("Success")

    val unknownThing: Any? = "Hi"

    if(unknownThing is Int){
        println("Hello!")
    }
    println(unknownThing as? Int)

    val a = 23L
    val b = 23f
    val c = 23
    val d = 23.0

    val unit: Unit = Unit
    val unitArmy = otherList.map { Unit }
}

@file:SharedCode
package com.test.classes

import com.lightningkite.khrysalis.*
import kotlin.math.absoluteValue
import kotlin.random.Random

interface TestInterface {
    val interfaceValue: String get() = "Default"
    fun interfaceFunction(): String = "Default"
}

data class DataClassThing(
    val x: Double = 0.0,
    val y: String = "Hello!",
    val z: DataClassThing? = null,
    val number: Int = 2
): TestInterface {
    override fun interfaceFunction(): String = "$x $y"
    @JsName("copyAlt")
    fun copy(alt: DataClassThing) = this.copy(alt.x, alt.y, alt.z)
}

@SwiftProtocolExtends("Codable", "Hashable")
interface ExtendsHashable {

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
}

class Box<T>(val value: T)

class SubWeird(a: Int, b: String, c: Double, d: Long): Weird(a, b, c, d) {
    override fun test() {
        super.test()
    }
}

fun main(){
    val outsideInfo: String = "Pulled in"
    val ugh = Weird(2)
    val box1 = Box(88)
    val box2 = Box<Int>(88)
    Random.nextInt()

    val instance = DataClassThing(y = "asdf")
    val copied = instance.copy(x = 10.0)
    val copiedAgain = instance.copy()
    val copiedAgain2 = instance.copy(number = 3)
    val copiedAlt = instance.copy(copied)
    println(copied == instance)
    println(copied == copiedAgain)
    println(copied === copiedAgain)
    println(copied === instance)
}
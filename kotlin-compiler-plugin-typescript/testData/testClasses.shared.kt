package com.test.classes

import com.test.magicVariable
import kotlin.math.absoluteValue

interface TestInterface {
    val interfaceValue: String get() = "Default"
    fun interfaceFunction(): String = "Default"
}

data class DataClassThing(val x: Double = 0.0, val y: String = "Hello!"): TestInterface {
    override fun interfaceFunction(): String = "$x $y"
}

class Weird(a: Int = 0, b: String, val c: Double, var d: Long): TestInterface {
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
}

fun main(){
    val outsideInfo: String = "Pulled in"
    val instance = object: TestInterface {
        override val interfaceValue: String
            get() = outsideInfo
    }
    val ugh = Weird(2)
}
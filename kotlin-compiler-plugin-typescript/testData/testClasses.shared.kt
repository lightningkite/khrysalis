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

    override val interfaceValue: String
        get() = f
}
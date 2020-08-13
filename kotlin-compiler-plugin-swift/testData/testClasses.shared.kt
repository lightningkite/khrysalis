package com.test

import com.test.magicVariable
import kotlin.math.absoluteValue
import kotlin.random.Random

private interface ClassesTestInterface {
    val interfaceValue: String get() = "Default"
    fun interfaceFunction(): String = "Default"
}

private data class ClassesDataClassThing(val x: Double = 0.0, val y: String = "Hello!"): ClassesTestInterface {
    override fun interfaceFunction(): String = "$x $y"
}

private open class ClassesWeird(a: Int = 0, b: String, val c: Double, var d: Long): ClassesTestInterface {
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
        return "$c $d"
    }

    override fun equals(other: Any?): Boolean {
        return other is ClassesWeird && this.d == other.d
    }

    override fun hashCode(): Int {
        return d.toInt()
    }
}

private open class ClassesComplexInit(val x: Int = 0) {
    val y = x
    val z = 0
    val a = this.x
    val b = run { this.y }
    val c = run { this.x }
    val d = 43 + run { this.x }
    val e = (43 + x)
    val f = (43 + x).let {
        it + this.z
    }
}

private class ClassesSubWeird(a: Int, b: String, c: Double, d: Long): ClassesWeird(a, b, c, d) {
    override fun test() {
        super.test()
    }
}

private fun classesMain(){
    val outsideInfo: String = "Pulled in"
    val instance = object: ClassesTestInterface {
        override val interfaceValue: String
            get() = outsideInfo
    }
    val ugh = ClassesWeird(2)
    Random.nextInt()
}
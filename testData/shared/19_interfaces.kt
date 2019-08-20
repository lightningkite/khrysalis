// Simple Interface
package com.lightningkite.interfaceTest

import com.lightningkite.others.*

interface MyInterface {
    fun bar(): String
    var x: Int
    val y: String
    val z: Float get() = 0f
}

class Implementation : MyInterface {
    override fun bar(): String {
        return "2-Implementation"
    }

    override var x: Int
        get() = 2
        set(value) {}
    override val y: String get() = "Hello World!"
}

// Interface + Inheritance

open class Parent {
    fun three(): Int {
        return 3
    }

    var x: Int
        get() = 2
        set(value) {}
    val y: String get() = "Hello World!"

    var four = 4
        set(value) {
            field = value
            println(value)
        }
}

class Child : Parent(), MyInterface {
    val five = 5
    fun six(): String {
        return "6"
    }

    override fun bar(): String {
        return "2-Child"
    }
}

fun main(args: Array<String>) {
    // Simple Interface
    val obj = Implementation()
    println(obj.foo())
    println(obj.bar())

    // Interface + Inheritance
    val child = Child()
    println(child.foo())
    println(child.bar())
    println(child.three())
    println(child.four)
    println(child.five)
    println(child.six())
}

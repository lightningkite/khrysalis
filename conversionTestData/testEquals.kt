@file:SharedCode
package com.test.equals

import com.lightningkite.khrysalis.*
import kotlin.math.absoluteValue
import kotlin.random.Random

open class Superclass() {
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is Superclass
}

class Subclass(val x: Int): Superclass() {
    override fun hashCode(): Int {
        return x
    }

    override fun equals(other: Any?): Boolean {
        return other is Subclass && other.x == x
    }
}

data class DataSubclass(val x: Int): Superclass()

fun main(){
    println(Subclass(2) == Superclass())
    println(Subclass(2) == Subclass(2))
    println(DataSubclass(2) == DataSubclass(2))
    println(DataSubclass(2) == Superclass())
    32.hashCode()
}
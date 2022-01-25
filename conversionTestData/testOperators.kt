@file:SharedCode
package com.test.operators

import com.lightningkite.khrysalis.*

class ExampleMutableThing {
    var containedNumber: Int = 0
    operator fun plus(other: ExampleMutableThing): ExampleMutableThing {
        val result = ExampleMutableThing()
        result.containedNumber = this.containedNumber + other.containedNumber
        return result
    }

    operator fun plusAssign(other: ExampleMutableThing) {
        this.containedNumber += other.containedNumber
    }

    operator fun get(index: Int): Int = containedNumber + index
    operator fun set(index: Int, value: Int): Int = value - index

    operator fun invoke() {
        println("Hello!")
    }
}

class ExampleImmutableThing {
    var containedNumber: Int = 0
    operator fun plus(other: ExampleImmutableThing): ExampleImmutableThing {
        val result = ExampleImmutableThing()
        result.containedNumber = this.containedNumber + other.containedNumber
        return result
    }

    operator fun unaryMinus(): ExampleImmutableThing {
        val result = ExampleImmutableThing()
        result.containedNumber = -this.containedNumber
        return result
    }

    operator fun get(index: Int): Int = containedNumber + index
}

fun main() {
    var x: Int = 2

    //Normal operators
    println(-x)
    println(2 + x)
    println(2 - x)
    println(2 * x)
    println(2 / x)
    println(2 % x)
    println(2 > x)
    println(2 < x)
    println(2 >= x)
    println(2 <= x)
    println(2 != x)
    println(2 == x)
    val objA = ExampleImmutableThing()
    val objB = ExampleImmutableThing()
    println(objA === objB)

    //Direct calls
    println(2.plus(x))
    println(2.minus(x))
    println(2.times(x))
    println(2.div(x))
    println(2.rem(x))

    x += if(x > 1) 2 else 0 //X
    println(x)
    x = 2

    //Null calls
    val maybeNumber: Int? = if(x > 1) x else null
    println(maybeNumber?.plus(2) ?: "nada")
    println(maybeNumber?.minus(2) ?: "nada")
    println(maybeNumber?.times(2) ?: "nada")
    println(maybeNumber?.div(2) ?: "nada")
    println(maybeNumber?.rem(2) ?: "nada")

    //Assign operators
    x = 2
    x += 1
    println(x)
    x = 2
    x -= 1
    println(x)
    x = 2
    x *= 2
    println(x)
    x = 2
    x /= 2
    println(x)
    x = 2
    x %= 1
    println(x)
    x = 2
    x = -x
    println(x)

    //Psuedo-operators
    x = 2
    println(x shl 1)
    println(x shr 1)
    println(x ushr 1)
    println(x and 1)
    println(x or 1)
    println(x xor 1)
    println(1.inv())

    //Overloading
    val thing1 = ExampleImmutableThing()
    thing1.containedNumber = 1
    val thing2 = ExampleImmutableThing()
    thing2.containedNumber = 2

    val maybe = if(x > 1) thing1 else null
    println(maybe == null)
    println(maybe != null)
    println(maybe === null)
    println(maybe !== null)
    println(null == maybe)
    println(null != maybe)
    println(null === maybe)
    println(null !== maybe)

    val thing3 = thing1 + thing2
    println(thing3.containedNumber)

    println(thing3[1])

    val thing4 = -thing3
    println(thing4.containedNumber)

    var thing5 = thing4
    thing5 += thing1
    println(thing5.containedNumber)

    val mthing1 = ExampleMutableThing()
    mthing1.containedNumber = 1
    val mthing2 = ExampleMutableThing()
    mthing2.containedNumber = 2
    mthing1 += mthing2
    println(mthing1.containedNumber)
    println(mthing1[2])
    mthing1[2] = 8
    mthing1[2] += 8
    println(mthing1[1])
    mthing1.invoke()
    mthing1()

    //Binary
    println(true && true)
    println(true || false)

    //Ranges
    for (num in 1..6) {
        println(num)
    }
    println(3 in 1..6)

    //Containers
    val choices = listOf(1, 2, 3, 4)
    println(5 in choices)
    println(5 !in choices)
}
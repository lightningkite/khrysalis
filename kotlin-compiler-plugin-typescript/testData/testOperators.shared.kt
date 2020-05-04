package com.test.operators

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
}

fun main() {
    //Normal operators
    println(1 + 2)
    println(1 - 2)
    println(1 * 2)
    println(1 / 2)
    println(1 % 2)
    println(1 > 2)
    println(1 < 2)
    println(1 >= 2)
    println(1 <= 2)
    println(1 != 2)
    println(1 == 2)

    //Direct calls
    println(1.plus(2))
    println(1.minus(2))
    println(1.times(2))
    println(1.div(2))
    println(1.rem(2))

    //Assign operators
    var x: Int = 0
    x += 1
    x -= 1
    x *= 2
    x /= 2
    x %= 1
    x = -x
    println(x)

    //Psuedo-operators
    x = x shl 1
    x = x shr 1
    x = x ushr 1
    x = x and 1
    x = x or 1
    x = x xor 1
    x = 1.inv()

    //Overloading
    val thing1 = ExampleImmutableThing()
    thing1.containedNumber = 1
    val thing2 = ExampleImmutableThing()
    thing2.containedNumber = 2

    val thing3 = thing1 + thing2
    println(thing3.containedNumber)

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
@file:SharedCode
package com.test.ifandwhen

import com.lightningkite.khrysalis.*

fun setNullable(): Int? = 2
fun makeSomething(): Any? = "Hello"

fun main() {
    var thing: Int = 0

    //If/else chaining

    if (thing == 1) {
        println("is 1")
    }

    if (thing == 0) {
        println("is zero")
    } else {
        println("is not zero")
    }

    if (thing == 0) {
        println("is zero")
    } else if (thing == 1) {
        println("is one")
    } else {
        println("is more")
    }

    //If nullable smart cast
    var thing2: Int? = setNullable()
    if (thing2 != null) {
        println("Thing is not null")
        val result = 3 + thing2
        println(result)
    }

    if (thing2 == null) {
        if(makeSomething() != null) {
            makeSomething()
        } else {
            makeSomething()
        }
    } else {
        if(makeSomething() != null) {
            makeSomething()
            thing += 1
        } else {
            makeSomething()
            thing += 1
        }
    }

    if (thing2 == null) {
        println("thing is null")
    } else {
        println("Thing is not null")
        val result = 3 + thing2
        println(result)
    }

    if (thing2 != null && thing == 0) {
        println(thing2 + thing)
    }

    val ifAsExpression = if (thing2 != null) thing2 else 0
    val ifAsExpression2 = if (thing2 != null) {
        println("Hi!")
        thing2
    } else {
        println("SAD")
        0
    }

    val ifAsExpression3 = listOf(1, 2, 3).map {
        if(it % 2 == 0) return@map it else return@map it + 1
    }

    // Double ternary
    println(if(thing % 2 == 0) 2 else if (thing % 3 == 0) 3 else 0)

    fun subfunction(): Int {
        return if (thing2 != null) thing2 else 0
    }

    fun subfunction2() {
        if (thing2 != null) {
            println("yeah")
            println("it's right here")
            thing++
        } else {
            println("No")
            thing++
        }
    }

    fun subfunction3(): Int {
        return if (thing2 != null) {
            println("Hi3!")
            thing2
        } else {
            println("SAD3")
            0
        }
    }

    if (thing == 0) {
        thing2?.let {
            println("It's a $it")
        }
        println("Did the thing")
    }

    //Safe let
    thing2?.let {
        println("It's a $it")
//        thing2?.let {
//            println("ANOTHER: $it")
//        }
    } ?: thing2?.let {
        println("It's a $it")
    } ?: run {
        println("Dunno what it is")
    }

    //Safe let no default
    thing2?.let {
        println(it + 1)
    } ?: thing2?.let {
        println(it * 2)
    }

    //Safe let single
    thing2?.let {
        if (subfunction3() == 0) {
            println("Hiii")
        }
    }

    //When on subject
    when (thing) {
        0 -> println("is zero")
        1 -> println("is one")
        2 -> {
            println("is two")
            println("which is magical")
        }
        3,4 -> println("Three or four")
        else -> 999
    }

    //When on subject as expression
    fun calcInverted() = when (thing) {
        0 -> 2
        1 -> 1
        2 -> 0
        else -> 999
    }

    //When on subject as expression with blocks
    fun calcInverted2() = when (thing) {
        0 -> { 2 }
        1 -> { 1 }
        2 -> { 0 }
        else -> 999
    }

    //When on conditions
    when {
        thing == 1 -> println("thing is one")
        thing2 != null -> println("thing2 is not null")
        else -> println("nah")
    }

    //When on conditions as expression
    fun calcWeird() = when {
        thing == 1 -> 0
        thing2 != null -> 3
        else -> 999
    }

    //TODO: Support in Swift
//    //When with throw expression
//    @Throws(IllegalStateException::class) fun calcWeirdThrows() = when {
//        thing == 1 -> 0
//        thing2 != null -> 3
//        else -> throw IllegalArgumentException("NO!")
//    }

    //When with throw
    @Throws(IllegalStateException::class) fun calcWeirdThrows(): Int {
        when {
            thing == 1 -> return 0
            thing2 != null -> return 3
            else -> throw IllegalArgumentException("NO!")
        }
    }

    //when on subject advanced
    when (thing2) {
        0 -> println("is zero")
        null -> println("is null")
        else -> println("uggh fine")
    }

    //when on subject typed
    val thing3: Any? = makeSomething()
    when (thing3) {
        is String -> println("Found string " + thing3)
        is Int -> println("Found int ${thing3}")
        null -> println("Found null")
        else -> println("uggh fine")
    }
}
package com.test

fun ifAndWhenSetNullable(): Int? = 2
fun ifAndWhenMakeSomething(): Any? = "Hello"

fun ifAndWhenMain() {
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
    var thing2: Int? = ifAndWhenSetNullable()
    if (thing2 != null) {
        println("Thing is not null")
        val result = 3 + thing2
        println(result)
    }

    if (thing2 == null) {
        if(ifAndWhenMakeSomething() != null) {
            ifAndWhenMakeSomething()
        } else {
            ifAndWhenMakeSomething()
        }
    } else {
        if(ifAndWhenMakeSomething() != null) {
            ifAndWhenMakeSomething()
            thing += 1
        } else {
            ifAndWhenMakeSomething()
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
    val ifAsExpression4 = if (thing2 != null && thing == 0) thing2 else 0
    val ifAsExpression2 = if (thing2 != null) {
        println("Hi!")
        thing2
    } else {
        println("SAD")
        null
    }

    val ifAsExpression3 = listOf(1, 2, 3).map {
        if(it % 2 == 0) return@map it else return@map it + 1
    }

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

    thing2?.let {
        println("It's a $it")
        thing2?.let {
            println("ANOTHER")
        }
    } ?: thing2?.let {
        println("It's a $it")
    } ?: run {
        println("Dunno what it is")
    }

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
        else -> {
            println("is something else")
        }
    }
    when (thing) {
        0 -> println("is zero")
        1 -> println("is one")
        2 -> {
            println("is two")
            println("which is magical")
        }
    }
    val whenAsExpression1 = when (thing) {
        0 -> "is zero"
        1 -> "is one"
        2 -> {
            "which is magical"
        }
    }

    //When on conditions
    when {
        thing == 1 -> println("thing is one")
        thing2 != null -> println("thing2 is not null")
        else -> println("else")
    }
    val whenAsExpression2 = when {
        thing == 1 -> "thing is one"
        thing2 != null -> "thing2 is not null"
        else -> "else"
    }
    val whenAsExpression3 = when {
        thing == 1 -> {
            thing2?.let { "heyyy" }
        }
        thing == 0 -> { "asdf" }
        thing2 != null -> thing2?.let { "heyyy" }
        else -> null
    }

    //when on subject advanced
    when (thing2) {
        0 -> println("is zero")
        null -> println("is null")
        else -> println("is something")
    }

    //when on subject typed
    var thing3: Any? = ifAndWhenMakeSomething()
    when (thing3) {
        is String -> println("Found string " + thing3)
        is Int -> println("Found int ${thing3}")
        null -> println("Found null")
        else -> println("Found something else")
    }
}
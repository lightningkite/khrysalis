package com.test.ifandwhen

fun setNullable(): Int? = 2
fun makeSomething(): Any? = "Hello"

fun main(){
    var thing: Int = 0

    //If/else chaining

    if(thing == 1){
        println("is 1")
    }

    if(thing == 0){
        println("is zero")
    } else {
        println("is not zero")
    }

    if(thing == 0){
        println("is zero")
    } else if(thing == 1){
        println("is one")
    } else {
        println("is more")
    }

    //If nullable smart cast
    var thing2: Int? = setNullable()
    if(thing2 != null){
        println("Thing is not null")
        val result = 3 + thing2
        println(result)
    }

    if(thing2 == null){
        println("thing is null")
    } else {
        println("Thing is not null")
        val result = 3 + thing2
        println(result)
    }

    if(thing2 != null && thing == 0){
        println(thing2 + thing)
    }

    //When on subject
    when(thing){
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

    //When on conditions
    when {
        thing == 1 -> println("thing is one")
        thing2 != null -> println("thing2 is not null")
        else -> println("else")
    }

    //when on subject advanced
    when(thing2){
        0 -> println("is zero")
        null -> println("is null")
        else -> println("is something")
    }

    //when on subject typed
    var thing3: Any? = makeSomething()
    when(thing3){
        is String -> println("Found string " + thing3)
        is Int -> println("Found int ${thing3}")
        null -> println("Found null")
        else -> println("Found something else")
    }
}
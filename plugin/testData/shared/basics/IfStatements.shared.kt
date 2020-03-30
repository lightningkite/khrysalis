package com.test

fun main(){
    var number: Int = 0
    if(true){
        number += 1
    } else {
        number -= 1
    }

    if(false){
        number += 2
    } else {
        number -= 2
    }

    var thing: String? = null
    thing?.let {
        number += 4
    } ?: run {
        number -= 4
    }

    thing = "Hello"
    thing?.let {
        number += 8
    } ?: run {
        number -= 8
    }

    thing?.substringBefore(",")?.let {
        number += 16
    } ?: run {
        number -= 16
    }

    if(thing != null){
        println(thing)
        number += 32
    }

    if(thing is String){
        println(thing)
        number += 64
    }

    println(number)
}

package com.test

fun main(){
    var number: Int = 0
    while(number < 10){
        number++
    }
    for(i in 0.toInt()..20.toInt()) {
        number += i
    }
    if(number % 2 == 0){
        number += 1
    } else {
        number -= 1
    }
    var value = 43.toInt()
    when(value){
        1 -> number += 1
        2 -> {
            number += 2
            number += 3
        }
        43 -> {
            number += 4
        }
        else -> {
            number -= 99
        }
    }
    when {
        value == 42 -> number += 8
        value == 41 -> {
            number += 16
        }
        value == 43 -> number += 32
        else -> {}
    }
    println(number)
}

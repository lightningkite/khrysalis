package com.test

fun main(){
    var number: Int = 0
    for(i in 0.toInt()..20.toInt()) {
        number += i
    }
    println(number)
}

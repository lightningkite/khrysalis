package com.test

fun main() {
    var number: Int = 0

    number += if (true) {
        2
    } else if (true) {
        var temp = 1
        temp
    }else if (1==1){
        var temp = 9
        9 + 10
    } else {
        0
    }

    val letCheckValue: String? = null
    number += letCheckValue?.let { 2 } ?: 0

    //TODO: Make this work
//    val checkValueWhen: Int = 2
//    number += when(checkValueWhen){
//        0 -> 2.toInt()
//        1 -> 1.toInt()
//        2 -> 0.toInt()
//        else -> -99.toInt()
//    }

    println(number)
}

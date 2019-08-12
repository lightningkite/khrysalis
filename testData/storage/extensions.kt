package com.test

fun Int.test(): String{
    return "The number doubled is: ${this * 2}"
}

fun <T> List<T>.print(){
    println(this.firstOrNull())
}

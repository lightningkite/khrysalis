package com.test

fun main(){
    println(addNumbers(left = 1, right = addNumbers2(2, addNumbers3(3, 4))))
}

fun addNumbers(left: Int, right: Int): Int = left + right
fun addNumbers2(left: Int, right: Int): Int {
    return left + right
}

fun addNumbers3(left: Int, right: Int): Int {
    fun subfunction(left: Int): Int {
        return left + right
    }
    return subfunction(left)
}

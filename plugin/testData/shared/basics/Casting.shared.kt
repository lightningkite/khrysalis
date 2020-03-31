package com.test

fun main(){
    var value: Any? = null
    val asString = value as? String
    value = "XP"
    val forced = value as String
    println(value ?: "Nope")
    println(forced)
}

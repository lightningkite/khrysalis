package com.test

fun main(){
    val lambda: (String, String)->Unit = { word1, word2 ->
        println("$word1 $word2!")
    }
    lambda("Hello", "world")
}

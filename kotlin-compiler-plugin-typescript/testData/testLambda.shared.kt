package com.test.lambda

class TestClass {
    var item: Int = 0
}

fun main(){
    val theAnswer = TestClass().apply { item = 42 }
    theAnswer.let {
        println(it)
    }
}
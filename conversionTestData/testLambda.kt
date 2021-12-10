@file:SharedCode
package com.test.lambda

import com.lightningkite.khrysalis.*

class TestClass {
    var item: Int = 0
    fun test(action: ()->Unit){
        action()
    }
    fun testRec(action: Int.()->Unit){
        action(2)
    }

    override fun toString(): String {
        return "Test item $item"
    }
}

fun main(){
    val theAnswer = TestClass().apply { item = 42 }
    val myLambda: (Int)->String = {
        "Number: $it"
    }
    theAnswer.let {
        println(it.toString())
    }
    32.let {
    }
    32.let {
        println(it)
    }
    32.let label@{
        println(it)
        println(it)
        return@label
    }

    val lambda: ()->Unit = label@{ ->
        println("Hi")
        return@label
    }
    val lambda2: (Int, String)->Unit = label@{ i, s ->
        println(s + i.toString())
        return@label
    }
    val lambda3: ()->Unit = label@{ ->
        if(theAnswer.item < 22){
            println("Hello!")
        } else {
            println("WRONG")
        }
    }
}
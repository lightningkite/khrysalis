package com.test

fun test(){

    val lambda = weakLambda { value: Int ->
        println(value)
    }

    val lambda2 = weakLambda { value: Int, second: String? ->
        println(value)
        return@weakLambda 2
    }
}

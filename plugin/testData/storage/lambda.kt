package com.test

fun lambdaTest() {
    takesLambda(lambda = { integer, someValue ->
        println("Hello!")
    })
    takesLambda { integer, someValue ->
        println("Hello!")
    }
}

fun takesLambda(lambda: (integer: Int, someValue: String) -> Unit) {

}

fun takesEscapingLambda(@escaping lambda: (integer: Int, someValue: String) -> Unit) {

}

class ConstructorHasEscaping(@escaping val lambda: (integer: Int, someValue: String) -> Unit) {
}

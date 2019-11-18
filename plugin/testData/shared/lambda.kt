package com.test

fun lambdaTest() {
    takesLambda(lambda = { integer, someValue ->
        println("Hello!")
    })
    takesLambda { integer, someValue ->
        println("Hello!")
    }
    takesLambda(label@ @unownedSelf { integer, someValue ->
        println("Hello!")
    })
}

fun takesLambda(lambda: @escaping() (integer: Int, someValue: String) -> Unit) {

}

fun takesPainfulLambda(lambda: @escaping() (integer: Int, someValue: @escaping() (String) -> Unit) -> Unit) {

}

fun takesEscapingLambda(lambda: @escaping() (integer: Int, someValue: String) -> Unit) {

}

fun takesEscapingLambda2(lambda: (@escaping() (integer: Int, someValue: String) -> Unit)) {

}

class ConstructorHasEscaping(val lambda: @escaping (integer: Int, someValue: String) -> Unit) {
}


class ConstructorHasEscaping2(val lambda: (@escaping (integer: Int, someValue: String) -> Unit)) {
}

class HeavenHelpUsAll(val lambda: @escaping() (integer: Int, someValue: @escaping() (Int) -> Unit) -> Unit) {
}

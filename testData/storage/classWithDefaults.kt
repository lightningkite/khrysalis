package com.test

class Something(argument: Int) {
    val test: Int = argument
    val weakRef: Int? by weak(argument)
}

package com.test

class NormalDeclaration(val x: Int, val y: String)

val NormalDeclaration.z: Int get() = x
fun NormalDeclaration.a(): String = y

fun test(){
    println(32 plus 32 minus 2 times 3)
    println(Int.MIN_VALUE)
    println(Int.MAX_VALUE)
    call(Unit)
}


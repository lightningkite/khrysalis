package com.test

import com.lightningkite.khrysalis.*

data class NameTag<T: Hashable>(val item: T, val name: String)

fun <T: MyInterface> NameTag<T>.printInterface(){
    println("Hello!  I am $name, I stand for ${item.x}.")
}

interface MyInterface {
    val x: String get() = ""
    fun y(string: String): String {
        return x + string
    }
}

data class ImplOverX(val k: Int = 0): MyInterface {
    override val x: String
        get() = "Hello!"
}

data class ImplOverY(val k: Int = 0): MyInterface {
    override fun y(string: String): String {
        return "$x!"
    }
}

data class ImplBoth(val k: Int = 0): MyInterface {
    override val x: String
        get() = "Hello!"
    override fun y(string: String): String {
        return "$x!"
    }
}

fun main(){
    NameTag(ImplBoth(), "ImplBoth").printInterface()
    NameTag(ImplOverX(), "ImplOverX").printInterface()
    NameTag(ImplOverY(), "ImplOverY").printInterface()
}


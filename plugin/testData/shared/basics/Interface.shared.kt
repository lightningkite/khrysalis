package com.test

interface MyInterface {
    val x: String get() = ""
    fun y(string: String): String {
        return x + string
    }
}

class ImplOverX: MyInterface {
    override val x: String
        get() = "Hello!"
}

class ImplOverY: MyInterface {
    override fun y(string: String): String {
        return "$x!"
    }
}

class ImplBoth: MyInterface {
    override val x: String
        get() = "Hello!"
    override fun y(string: String): String {
        return "$x!"
    }
}

fun main(){
    val items: List<MyInterface> = listOf(
        ImplBoth(),
        ImplOverX(),
        ImplOverY()
    )
    for(item in items){
        println(item.x)
        println(item.y("Input"))
    }
}

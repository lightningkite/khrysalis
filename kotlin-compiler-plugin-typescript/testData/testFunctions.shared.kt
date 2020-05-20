package com.test.functions

import com.test.JsName
import com.lightningkite.khrysalis.bytes.Data

fun topLevelFunction(){
    println("Hello world!")
    fun localFunction(){
        println("Hello from local!")
    }
    localFunction()
}
fun <T> topLevelGenericFunction(item: T){
    println("Hello ${item}")
}
@JsName("restParamsFunction")
fun varargFunction(vararg numbers: Int){
    for(item in numbers){
        println("Item: $item")
    }
}

class TestClass {
    val member: Int? = 0
    fun memberFunction(a: Int = 2, b: Int = 3, c: Int = 4){
        println("Hello from TestClass!")
    }
    fun <T> memberGenericFunction(item: T){
        println("Hello ${item} from TestClass!")
    }
    fun TestClass2.memberExtensionFunction(){
        println("Hello ${this} from ${this@TestClass}!")
    }
    fun testExtension(){
        TestClass2().memberExtensionFunction()
    }
    fun otherTest(){
        member?.let {
            println(it)
        }
        val x = member?.let {
            it + 2
        }
        this.member?.let {
            println(it)
        }
        val y = member?.let {
            it + 1
        } ?: member?.let {
            it + 1
        } ?: 0
        memberFunction()
        this.memberFunction()
    }
}

class TestClass2 {
    fun test(){
        println("Hi!")
    }
}

fun TestClass.extensionFunction(){
    println("From an extension:")
    this.memberFunction()
}
fun <T, E> T.genericExtensionFunction(element: E){
    println("Hello $element from $this!")
}

inline fun <reified T> resolve(): T? {
    return null
}

fun main(){
    topLevelFunction()
    topLevelGenericFunction(2)
    varargFunction(1, 2, 3, 4, 5)
    val instance = TestClass()
    instance.memberFunction()
    instance.memberFunction(a = 1)
    instance.memberFunction(b = 1)
    instance.memberFunction(c = 1)
    instance.memberFunction(c = 1, a = 2)
    instance.memberGenericFunction(32 /*comment*/)
    instance.testExtension()
    instance.extensionFunction()
    instance.genericExtensionFunction(8)
    val x: Int? = resolve()
    val y = resolve<String>()
}

val anotherThing = 2

fun testRepl(data: Data) {
    println(data.size)
}
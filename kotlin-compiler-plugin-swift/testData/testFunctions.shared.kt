package com.test

import com.test.JsName
import com.lightningkite.khrysalis.bytes.Data

private fun topLevelFunction(){
    println("Hello world!")
    fun localFunction(){
        println("Hello from local!")
    }
    localFunction()
}
private fun <T> topLevelGenericFunction(item: T){
    println("Hello ${item}")
}
@JsName("restParamsFunction")
private fun varargFunction(vararg numbers: Int){
    for(item in numbers){
        println("Item: $item")
    }
}

private class FunctionTestClass {
    val member: Int? = 0
    fun memberFunction(a: Int = 2, b: Int = 3, c: Int = 4){
        println("Hello from FunctionTestClass!")
    }
    fun <T> memberGenericFunction(item: T){
        println("Hello ${item} from FunctionTestClass!")
    }
    fun FunctionTestClass2.memberExtensionFunction(){
        println("Hello ${this} from ${this@FunctionTestClass}!")
    }
    fun testExtension(){
        FunctionTestClass2().memberExtensionFunction()
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
        member?.let {
            it + 1
        } ?: member?.let {
            it + 1
        } ?: run {
            println("hi")
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

private class FunctionTestClass2 {
    private fun test(){
        println("Hi!")
    }
}

private fun FunctionTestClass.extensionFunction(){
    println("From an extension:")
    this.memberFunction()
}
@JsName("extensionFunction2")
private fun FunctionTestClass.extensionFunction(str: String){
    println("From an extension with $str:")
    this.memberFunction()
}
private fun <T, E> T.genericExtensionFunction(element: E){
    println("Hello $element from $this!")
}

private inline fun <reified T> functionResolve(): T? {
    return null
}

private fun functionMain(){
    topLevelFunction()
    topLevelGenericFunction(2)
    varargFunction(1, 2, 3, 4, 5)
    val instance = FunctionTestClass()
    instance.memberFunction()
    instance.memberFunction(a = 1)
    instance.memberFunction(b = 1)
    instance.memberFunction(c = 1)
    instance.memberFunction(c = 1, a = 2)
    instance.memberGenericFunction(32 /*comment*/)
    instance.testExtension()
    instance.extensionFunction()
    instance.extensionFunction("asdf")
    instance.genericExtensionFunction(8)
    val x: Int? = functionResolve()
    val y = functionResolve<String>()
}

private val functionAnotherThing = 2

private fun functionTestRepl(data: Data) {
    println(data.size)
}
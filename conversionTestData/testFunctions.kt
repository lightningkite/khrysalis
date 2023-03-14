@file:SharedCode
package com.test.functions

import com.lightningkite.khrysalis.*

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

fun TestClass.Companion.companionExtension() {
    println("Companion extension")
}

class TestClass {
    companion object {
        fun companionMember() {
            println("Hi!")
        }
    }
    var member: Int? = null
    var member2: Int = 1
    fun chain(): TestClass {
        return this
    }
    fun memberFunction(a: Int = 2, b: Int = 3, c: Int = 4){
        println("Hello from TestClass!")
        println(member2)
        println(this.member2)
    }
    fun <T> memberGenericFunction(item: T){
        println("Hello ${item} from TestClass!")
        println(member2)
        println(this.member2)
    }
    fun TestClass2.memberExtensionFunction(){
        println("Hello ${this} from ${this@TestClass}!")
        println(member2)
        println(this@TestClass.member2)
    }
    infix fun <T> TestClass2.memberExtensionGenericFunction(item: T){
        println("Hello ${this} from ${this@TestClass}!")
        println(member2)
        println(this@TestClass.member2)
    }
    infix fun <T> GenericBox<T>.memberExtensionGenericFunction2(value: T){
        println("Hello ${this.t} from ${this@TestClass}!")
        println(member2)
        println(this@TestClass.member2)
    }
    fun testExtension(){
        TestClass2().memberExtensionFunction()
        TestClass2().memberExtensionGenericFunction<Int>(2)
        TestClass2() memberExtensionGenericFunction 2
        GenericBox(2) memberExtensionGenericFunction2 2
    }
    @Throws(IllegalStateException::class)
    fun mayThrow() = member ?: throw IllegalStateException()
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
        extensionFunction()
        this.extensionFunction()
    }
    fun memberVarargTestFunction(vararg values: Int) {
        for(item in values){
            println(item)
        }
    }

    override fun toString(): String {
        return "TestClass"
    }
}

class TestClass2 {
    fun test(){
        println("Hi!")
    }

    override fun toString(): String {
        return "TestClass2"
    }
}

fun TestClass.extensionFunction(){
    println("From an extension:")
    this.memberFunction()
    println(member2)
    println(this.member2)
}
@JsName("extensionFunction2")
fun TestClass.extensionFunction(str: String){
    println("From an extension with $str:")
    this.memberFunction()
    println(member2)
    println(this.member2)
}
infix fun TestClass.extensionFunctionInfix(str: String){
    println("From an extension with $str:")
    this.memberFunction()
    println(member2)
    println(this.member2)
}
fun <T, E> T.genericExtensionFunction(element: E){
    println("Hello $element from $this!")
}

class GenericBox<T>(val t: T)

fun <T> GenericBox<T>.ext1() {
    println(t)
    println(this.t)
}
fun GenericBox<Int>.ext2() {
    println(t)
    println(this.t)
}
fun <T: Comparable<T>> GenericBox<T>.ext3() {
    println(t)
    println(this.t)
}
fun <T> GenericBox<List<T>>.ext4() {
    println(t.first())
    println(this.t.first())
}
fun <T> GenericBox<()->T>.ext5() {
    t()
    this.t()
}

fun varargTestFunction(vararg values: Int) {
    for(item in values){
        println(item)
    }
}

inline fun <reified T> resolve(): T? {
    return null
}

fun main(){
    topLevelFunction()
    topLevelGenericFunction(2)
    varargFunction(1, 2, 3, 4, 5)
    val instance = TestClass()
    instance.member = 2
    instance.memberFunction()
    instance.memberFunction(a = 1)
    instance.memberFunction(b = 1)
    instance.memberFunction(c = 1)
    instance.memberFunction(c = 1, a = 2)
    instance.memberGenericFunction(32 /*comment*/)
    instance.testExtension()
    instance.extensionFunction()
    instance.extensionFunction("asdf")
    instance extensionFunctionInfix "asdf"
    instance.genericExtensionFunction(8)
    instance.memberVarargTestFunction(1,2,3,4)
    instance.memberVarargTestFunction()
    instance.mayThrow()

    val two = TestClass2()
    with(instance) {
        two.memberExtensionFunction()
        two.memberExtensionGenericFunction(3)
        two memberExtensionGenericFunction 3
        GenericBox(2) memberExtensionGenericFunction2 2
    }

    val maybeInstance: TestClass? = if(instance.member == 2) instance else null
    maybeInstance?.member = 2
    maybeInstance?.chain()?.chain()?.chain()?.chain()
    maybeInstance?.memberFunction()
    maybeInstance?.memberFunction(a = 1)
    maybeInstance?.memberFunction(b = 1)
    maybeInstance?.memberFunction(c = 1)
    maybeInstance?.memberFunction(c = 1, a = 2)
    maybeInstance?.memberGenericFunction(32 /*comment*/)
    maybeInstance?.testExtension()
    maybeInstance?.extensionFunction()
    maybeInstance?.extensionFunction("asdf")
    maybeInstance?.genericExtensionFunction(8)
    maybeInstance?.memberVarargTestFunction(1,2,3,4)
    maybeInstance?.memberVarargTestFunction()

    val result0 = maybeInstance?.chain()?.chain()?.chain()?.chain()
    maybeInstance?.member = 2
    val result2 = maybeInstance?.memberFunction()
    val result3 = maybeInstance?.memberFunction(a = 1)
    val result4 = maybeInstance?.memberFunction(b = 1)
    val result5 = maybeInstance?.memberFunction(c = 1)
    val result6 = maybeInstance?.memberFunction(c = 1, a = 2)
    val result7 = maybeInstance?.memberGenericFunction(32 /*comment*/)
    val result8 = maybeInstance?.testExtension()
    val result9 = maybeInstance?.extensionFunction()
    val result10 = maybeInstance?.extensionFunction("asdf")
    val result11 = maybeInstance?.genericExtensionFunction(8)
    val result12 = maybeInstance?.memberVarargTestFunction(1,2,3,4)
    val result13 = maybeInstance?.memberVarargTestFunction()

    val x: Int? = resolve()
    val y = resolve<String>()
    varargTestFunction()
    varargTestFunction(1, 2, 3, 4)

    val box = GenericBox(42)
    box.ext1()
    box.ext2()
    box.ext3()
    GenericBox(listOf(1, 2, 3)).ext4()
    GenericBox({ 42 }).ext5()

    TestClass.companionExtension()
    TestClass.Companion.companionExtension()
    TestClass.companionMember()
    TestClass.Companion.companionMember()
}

val anotherThing = 2
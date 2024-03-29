@file:SharedCode
package com.test.variables

import kotlin.math.absoluteValue
import com.lightningkite.khrysalis.*

private var fileReal: Int = 0
var topLevelReal: Int = 0
var topLevelVirtual: Int
    get() = 1
    set(value){
        println("Attempted to set ${value}")
    }
var topLevelHybrid: Int = 2
    set(value){
        field = value + 1
    }
val topLevelLazy: Int by lazy {
    topLevelReal + 1
}

fun topLevelUsage(){
    topLevelReal = -1
    println(topLevelReal)
    topLevelVirtual = -2
    topLevelVirtual += 3
    println(topLevelVirtual)
    topLevelHybrid = -3
    println(topLevelHybrid)
    println(topLevelLazy)
}

class TestClass {
    var memberReal: Int = 0
    var memberVirtual: Int
        get() = 1
        set(value){
            println("Attempted to set ${value}")
        }
    var memberHybrid: Int = 2
        set(value){
            field = value + 1
        }
    val memberLambda: (Int)->Unit = {
        println(it)
    }
    val memberLazy: Int by lazy {
        memberReal + 1
    }
    val box = Box("HI", 0)
    init {
        box.item = 3
        this.box.item = 3
    }
    fun memberUsage(){

        memberLambda(1)
        this.memberLambda(1)

        memberReal = -1
        memberReal += -1
        println(memberReal)

        this.memberReal = -1
        this.memberReal += -1
        println(this.memberReal)

        memberVirtual = -2
        memberVirtual += -2
        println(memberVirtual)

        this.memberVirtual = -2
        this.memberVirtual += -2
        println(this.memberVirtual)

        memberHybrid = -3
        memberHybrid += -3
        println(memberHybrid)

        this.memberHybrid = -3
        this.memberHybrid += -3
        println(this.memberHybrid)

        println(this.memberLazy)

        extensionProperty = -4
        extensionProperty += -4
        println(extensionProperty)

        this.extensionProperty = -4
        this.extensionProperty += -4
        println(this.extensionProperty)

        topLevelReal = -1
        println(topLevelReal)
        topLevelVirtual = -2
        println(topLevelVirtual)
        topLevelHybrid = -3
        println(topLevelHybrid)

        TestClass.companionReal = -1
        println(TestClass.companionReal)
        TestClass.companionVirtual = -2
        println(TestClass.companionVirtual)
        TestClass.companionHybrid = -3
        println(TestClass.companionHybrid)

        Companion.companionReal = -1
        println(Companion.companionReal)
        Companion.companionVirtual = -2
        println(Companion.companionVirtual)
        Companion.companionHybrid = -3
        println(Companion.companionHybrid)

        companionReal = -1
        println(companionReal)
        companionVirtual = -2
        println(companionVirtual)
        companionHybrid = -3
        println(companionHybrid)
    }

    companion object {
        var companionReal: Int = 0
        var companionVirtual: Int
            get() = 1
            set(value){
                println("Attempted to set ${value}")
            }
        var companionHybrid: Int = 2
            set(value){
                field = value + 1
            }
    }
}

var TestClass.extensionProperty: Int
    get() = memberReal
    set(value){
        memberReal = value
    }

object TestObject {
    var objectReal: Int = 0
    var objectVirtual: Int
        get() = 1
        set(value){
            println("Attempted to set ${value}")
        }
    var objectHybrid: Int = 2
        set(value){
            field = value + 1
        }
    val objectLazy: Int by lazy {
        objectReal + 1
    }
    fun objectUsage(){
        objectReal = -1
        println(objectReal)
        objectVirtual = -2
        println(objectVirtual)
        objectHybrid = -3
        println(objectHybrid)

        topLevelReal = -1
        println(topLevelReal)
        topLevelVirtual = -2
        println(topLevelVirtual)
        topLevelHybrid = -3
        println(topLevelHybrid)

        val testInstance = TestClass()
        testInstance.needlesslyComplex = -4
        println(testInstance.needlesslyComplex)

        println(objectLazy)
    }
    var TestClass.needlesslyComplex: Int
        get() = memberReal
        set(value) {
            memberReal = value
            objectReal = value
        }
}

data class Box<T: IsCodableAndHashable>(val description: String, var item: T): Codable

class GenericTest<T> {
}
val <T> GenericTest<T>.ext: Int
    get() = 1

var TestObject.extensionProperty: Int
    get() = this.objectReal
    set(value){
        this@extensionProperty.objectReal = value
    }

var maybeInstance: TestClass? = null
fun main(){
    val instance = TestClass()
    fileReal += 1
    println(TestObject.objectReal)
    println(instance.memberReal)
    println(instance.extensionProperty)

    var local = 0
    local = 1
    local += 1
    local++
    println(local)

    topLevelReal = 1
    topLevelReal += 1
    topLevelReal++
    println(topLevelReal)

    topLevelVirtual = 1
    topLevelVirtual += 1
    topLevelVirtual++
    println(topLevelVirtual)

    topLevelHybrid = 1
    topLevelHybrid += 1
    topLevelHybrid++
    println(topLevelHybrid)

    println(topLevelLazy)

    instance.memberLambda(1)
    instance.memberLambda.invoke(1)

    instance.memberReal = 1
    instance.memberReal += 1
    instance.memberReal++
    println(instance.memberReal)

    instance.memberVirtual = -2
    instance.memberVirtual += -2
    instance.memberVirtual++
    println(instance.memberVirtual)

    instance.memberHybrid = -3
    instance.memberHybrid += -3
    instance.memberHybrid++
    println(instance.memberHybrid)

    println(instance.memberLazy)

    instance.extensionProperty = -4
    instance.extensionProperty += -4
    instance.extensionProperty++
    println(instance.extensionProperty)

    instance.box.item = -4
    instance.box.item += -4
    instance.box.item++
    println(instance.box.item)

    maybeInstance?.memberReal = 1
    println(maybeInstance?.memberReal ?: "nope")
    maybeInstance?.memberVirtual = -2
    println(maybeInstance?.memberVirtual ?: "nope")
    maybeInstance?.memberHybrid = -3
    println(maybeInstance?.memberHybrid ?: "nope")
    maybeInstance?.extensionProperty = -4
    println(maybeInstance?.extensionProperty ?: "nope")

    val maybeInstance2 = maybeInstance
    maybeInstance2?.memberReal = 1
    println(maybeInstance2?.memberReal ?: "nope")
    maybeInstance2?.memberVirtual = -2
    println(maybeInstance2?.memberVirtual ?: "nope")
    maybeInstance2?.memberHybrid = -3
    println(maybeInstance2?.memberHybrid ?: "nope")
    maybeInstance2?.extensionProperty = -4
    println(maybeInstance2?.extensionProperty ?: "nope")
}

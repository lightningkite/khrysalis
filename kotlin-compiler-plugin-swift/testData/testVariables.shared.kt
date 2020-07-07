package com.test.variables

import com.test.magicVariable
import kotlin.math.absoluteValue

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

fun topLevelUsage(){
    topLevelReal = -1
    println(topLevelReal)
    topLevelVirtual = -2
    topLevelVirtual += 3
    println(topLevelVirtual)
    topLevelHybrid = -3
    println(topLevelHybrid)
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
    }
    var TestClass.needlesslyComplex: Int
        get() = memberReal
        set(value) {
            memberReal = value
            objectReal = value
        }
}

class GenericTest<T> {
}
val <T> GenericTest<T>.ext: Int
    get() = 1
val GenericTest<Int>.ext2: Int
    get() = 1
val GenericTest<Any>.ext3: Int
    get() = 1

var TestObject.extensionProperty: Int
    get() = this.objectReal
    set(value){
        this@extensionProperty.objectReal = value
    }

private fun test(){
    val instance = TestClass()
    fileReal += 1
    println(TestObject.objectReal)
    println(instance.memberReal)
    println(instance.extensionProperty)
    println(magicVariable)
    magicVariable = 9001.absoluteValue.plus(4)
}

var maybeInstance: TestClass? = null
fun testNullable(){
    maybeInstance?.memberReal = -1
    println(maybeInstance?.memberReal)
    maybeInstance?.memberVirtual = -2
    println(maybeInstance?.memberVirtual)
    maybeInstance?.memberHybrid = -3
    println(maybeInstance?.memberHybrid)
    maybeInstance?.extensionProperty = -4
    println(maybeInstance?.extensionProperty)
    val instance = maybeInstance
    instance?.memberReal = -1
    println(instance?.memberReal)
    instance?.memberVirtual = -2
    println(instance?.memberVirtual)
    instance?.memberHybrid = -3
    println(instance?.memberHybrid)
    instance?.extensionProperty = -4
    println(instance?.extensionProperty)
}
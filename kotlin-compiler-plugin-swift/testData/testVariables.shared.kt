package com.test

import com.test.magicVariable
import kotlin.math.absoluteValue

private var variableFileReal: Int = 0
private var variableTopLevelReal: Int = 0
private var variableTopLevelVirtual: Int
    get() = 1
    set(value){
        println("Attempted to set ${value}")
    }
private var variableTopLevelHybrid: Int = 2
    set(value){
        field = value + 1
    }

private fun variableTopLevelUsage(){
    variableTopLevelReal = -1
    println(variableTopLevelReal)
    variableTopLevelVirtual = -2
    variableTopLevelVirtual += 3
    println(variableTopLevelVirtual)
    variableTopLevelHybrid = -3
    println(variableTopLevelHybrid)
}

private class VariableTestClass {
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

        variableTopLevelReal = -1
        println(variableTopLevelReal)
        variableTopLevelVirtual = -2
        println(variableTopLevelVirtual)
        variableTopLevelHybrid = -3
        println(variableTopLevelHybrid)

        VariableTestClass.companionReal = -1
        println(VariableTestClass.companionReal)
        VariableTestClass.companionVirtual = -2
        println(VariableTestClass.companionVirtual)
        VariableTestClass.companionHybrid = -3
        println(VariableTestClass.companionHybrid)

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

private var VariableTestClass.extensionProperty: Int
    get() = memberReal
    set(value){
        memberReal = value
    }

private object VariableTestObject {
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

        variableTopLevelReal = -1
        println(variableTopLevelReal)
        variableTopLevelVirtual = -2
        println(variableTopLevelVirtual)
        variableTopLevelHybrid = -3
        println(variableTopLevelHybrid)

        val testInstance = VariableTestClass()
        testInstance.needlesslyComplex = -4
        println(testInstance.needlesslyComplex)
    }
    var VariableTestClass.needlesslyComplex: Int
        get() = memberReal
        set(value) {
            memberReal = value
            objectReal = value
        }
}

private class VariableGenericTest<T> {
}
private val <T> VariableGenericTest<T>.ext: Int
    get() = 1
private val VariableGenericTest<Int>.ext2: Int
    get() = 1
private val VariableGenericTest<Any>.ext3: Int
    get() = 1

private var VariableTestObject.extensionProperty: Int
    get() = this.objectReal
    set(value){
        this@extensionProperty.objectReal = value
    }

private fun variableTest(){
    val instance = VariableTestClass()
    variableFileReal += 1
    println(VariableTestObject.objectReal)
    println(instance.memberReal)
    println(instance.extensionProperty)
    println(magicVariable)
    magicVariable = 9001.absoluteValue.plus(4)
}

private var variableMaybeInstance: VariableTestClass? = null
private fun variableTestNullable(){
    variableMaybeInstance?.memberReal = -1
    println(variableMaybeInstance?.memberReal)
    variableMaybeInstance?.memberVirtual = -2
    println(variableMaybeInstance?.memberVirtual)
    variableMaybeInstance?.memberHybrid = -3
    println(variableMaybeInstance?.memberHybrid)
    variableMaybeInstance?.extensionProperty = -4
    println(variableMaybeInstance?.extensionProperty)
    val instance = variableMaybeInstance
    instance?.memberReal = -1
    println(instance?.memberReal)
    instance?.memberVirtual = -2
    println(instance?.memberVirtual)
    instance?.memberHybrid = -3
    println(instance?.memberHybrid)
    instance?.extensionProperty = -4
    println(instance?.extensionProperty)
}
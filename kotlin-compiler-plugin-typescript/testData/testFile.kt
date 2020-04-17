package com.test

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
    fun memberUsage(){
        memberReal = -1
        println(memberReal)
        memberVirtual = -2
        println(memberVirtual)
        memberHybrid = -3
        println(memberHybrid)
    }
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
    }
}

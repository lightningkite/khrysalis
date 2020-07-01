package com.test.imports

import com.test.cursedclasses.*
import com.test.variables.*
import com.test.*

fun main(){
    A()
    A.B()
    A.B.C()
    A.B.C.D()
    val testObj = object: A.B.C.D.E {
        val x: Int = 0
    }
    println(F.H.x)

    topLevelReal += 1
    topLevelVirtual += 1
    topLevelHybrid += 1

    val thing = TestClass()
    thing.extensionProperty += 1

    sayDelicious()
    sayDeliciousFromOtherLib()
}
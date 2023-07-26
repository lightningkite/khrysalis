package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

class PointedTest {
    @Test
    fun test() {
        if(!hasSwift) return
        println(KotlinVersion.CURRENT)
        val it = File("../conversionTestData").resolve("testCodable.kt")
        val r = ExecuteFileTester.swiftTranslated(it)
        Assert.assertEquals(r.kotlin, r.swift)
        setOf(1, 2) + setOf(3)
    }
}

//test[testReplacements.kt]
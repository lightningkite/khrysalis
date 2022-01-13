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
//        println(swiftInstallation())
        val it = File("../conversionTestData").resolve("testFunctions.kt")
        val ktResult = ExecuteFileTester.kotlin(it, true)
        val jsResult = ExecuteFileTester.swiftTranslated(it)
        Assert.assertEquals(ktResult, jsResult)
    }
}

/*
test[testTime.kt]
test[testCastRule.kt]
test[testFunctions.kt]
 */
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
        val it = File("../conversionTestData").resolve("testEnums.kt")
        val ktResult = ExecuteFileTester.kotlin(it, true)
        val jsResult = ExecuteFileTester.swiftTranslated(it)
        Assert.assertEquals(ktResult, jsResult)
    }
}
//test[testVariables.kt]
//test[testCodable.kt]
//test[testEnums.kt]
//test[testGenerics.kt]
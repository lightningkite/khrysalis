package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

class PointedTest {

    @Test
    fun test() {
        val it = File("../conversionTestData").resolve("testTime.kt")
        println(it.absolutePath)
        val ktResult = ExecuteFileTester.kotlin(it, true)
        val jsResult = ExecuteFileTester.tsTranslated(it)
        Assert.assertEquals(ktResult, jsResult)
    }
}

//test[testClasses.kt]
//test[testIfAndWhen.kt]
//test[testExceptions.kt]
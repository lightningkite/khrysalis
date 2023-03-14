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
        val it = File("../conversionTestData").resolve("testFunctions.kt")
        val r = ExecuteFileTester.tsTranslated(it)
        Assert.assertEquals(r.kotlin, r.typescript)
    }
}
//test[testTime.kt]
//test[testSealed.kt]
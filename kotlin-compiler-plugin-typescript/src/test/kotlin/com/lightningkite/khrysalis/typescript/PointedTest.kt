package com.lightningkite.khrysalis.typescript

import com.fasterxml.jackson.databind.ObjectMapper
import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import com.lightningkite.khrysalis.replacements.Replacements
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

class PointedTest {

    @Test
    fun test() {
//        TypescriptTranslator("asdf", "asdf", null, Replacements(ObjectMapper()))
        val it = File("../conversionTestData").resolve("testEnums.kt")
        println(it.absolutePath)
        val ktResult = ExecuteFileTester.kotlin(it, true)
        val jsResult = ExecuteFileTester.tsTranslated(it)
        Assert.assertEquals(ktResult, jsResult)
    }
}

//test[testCodable.kt]
//test[testEnums.kt]
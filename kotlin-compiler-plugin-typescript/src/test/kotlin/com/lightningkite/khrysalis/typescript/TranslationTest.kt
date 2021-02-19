package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class TranslationTest {

    @Parameterized.Parameter(0)
    @JvmField
    var file: String = ""

    companion object {
        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun data(): Array<Array<String>> = arrayOf(
            arrayOf("testAnnotations.kt"),
            arrayOf("testArray.kt"),
            arrayOf("testBoolean.kt"),
            arrayOf("testClasses.kt"),
            arrayOf("testCodable.kt"),
            arrayOf("testCollections.kt"),
            arrayOf("testCursedClasses.kt"),
            arrayOf("testEnums.kt"),
            arrayOf("testExceptions.kt"),
            arrayOf("testFunctions.kt"),
            arrayOf("testIfAndWhen.kt"),
            arrayOf("testLambda.kt"),
            arrayOf("testLoops.kt"),
            arrayOf("testNulls.kt"),
            arrayOf("testOperators.kt"),
            arrayOf("testTypes.kt"),
            arrayOf("testVariables.kt")
        )
    }

    @Test
    fun test() {
        val it = File("./testData").resolve(file)
        val ktResult = ExecuteFileTester.kotlin(it, true)
        val jsResult = ExecuteFileTester.tsTranslated(it)
        Assert.assertEquals(ktResult, jsResult)
    }
}

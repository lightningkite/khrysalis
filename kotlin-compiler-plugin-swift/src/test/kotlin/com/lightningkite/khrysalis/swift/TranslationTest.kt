package com.lightningkite.khrysalis.swift

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
        fun data(): Array<Array<String>> = File("../conversionTestData").list()!!.filter { it.startsWith("test") }.map { arrayOf(it) }.toTypedArray()
    }

    @Test
    fun test() {
        Runtime.getRuntime().freeMemory().let { println("Free: $it") }
        if(!hasSwift) return
        val it = File("../conversionTestData").resolve(file)
        val r = ExecuteFileTester.swiftTranslated(it)
        Assert.assertEquals(r.kotlin, r.swift)
    }
}

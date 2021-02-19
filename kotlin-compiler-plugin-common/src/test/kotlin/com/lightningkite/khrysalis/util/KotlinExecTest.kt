package com.lightningkite.khrysalis.util

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import org.junit.Assert
import org.junit.Test
import java.io.File

class KotlinExecTest {
    @Test
    fun testExec() {
        val out = ExecuteFileTester.kotlin(
            ExecuteFileTester.tempFile("""
                @file:SharedCode
                package com.test
                
                import com.lightningkite.butterfly.SharedCode
               
                fun main(vararg args: String) {
                    println("Hello world!")
                }
                """.trimIndent())
        )
        Assert.assertEquals(out, "Hello world!")
    }
}
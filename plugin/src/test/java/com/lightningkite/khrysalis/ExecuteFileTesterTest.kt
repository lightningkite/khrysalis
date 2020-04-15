package com.lightningkite.khrysalis

import org.junit.Test
import java.io.File

class ExecuteFileTesterTest {
    val buildDir = System.getProperty("java.io.tmpdir").let { File(it) }.resolve("codeTranslationTestingSources")
        .also { it.mkdirs() }

    @Test
    fun checkTester() {
        val outputs = mapOf(
            "kotlin" to ExecuteFileTester.kotlin(
                buildDir.resolve("test.kt").also {
                    it.writeText("""
                        package com.tests
                        fun main(){
                            print("Hello World!")
                        }
                    """.trimIndent())
                },
                clean = true
            ),
            "swift" to ExecuteFileTester.swift(
                buildDir.resolve("test.swift").also {
                    it.writeText("""
                        import Foundation
                        func main(){
                            print("Hello World!")
                        }
                    """.trimIndent())
                },
                clean = true
            ),
            "typescript" to ExecuteFileTester.typescript(
                buildDir.resolve("test.ts").also {
                    it.writeText("""
                        export function main() {
                            console.log("Hello World!")
                        }
                    """.trimIndent())
                },
                clean = true
            )
        )
        for((key, value) in outputs){
            println("Language $key yielded $value")
        }
        val first = outputs.values.first()
        assert(outputs.values.all { it == first })
    }
}

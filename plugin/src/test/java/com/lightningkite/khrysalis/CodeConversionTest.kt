package com.lightningkite.khrysalis

import com.lightningkite.khrysalis.swift.convertKotlinToSwiftByFolder
import org.junit.Test
import java.io.File

class CodeConversionTest {

    @Test
    fun everything() {
        val kotlinFiles = File("./testData/shared")
        val swiftFiles = File("./testDataSwift/shared")
        val typescriptFiles = File("./testDataTypescript/shared")


        val isMac = System.getProperty("os.name").toLowerCase().let { it.contains("osx") || it.contains("mac") }

        if (isMac) {
            convertKotlinToSwiftByFolder(
                interfacesOut = File("./testDataSwift/interfaces.json").also { it.parentFile.mkdirs() },
                baseKotlin = File("./testData/shared"),
                baseSwift = File("./testDataSwift/shared").also { it.mkdirs() },
                clean = true,
                setup = {
                    this.imports = listOf()
                }
            )
        }

        val results = kotlinFiles.walkTopDown().filter { it.name.endsWith(".shared.kt") }.associate { kotlinFile ->
            val relative = kotlinFile.relativeTo(kotlinFiles)
            val swiftFile = swiftFiles.resolve(relative).parentFile
                .resolve(kotlinFile.nameWithoutExtension + ".swift")
            val typescriptFile = typescriptFiles.resolve(relative).parentFile
                .resolve(kotlinFile.nameWithoutExtension + ".ts")
            var expectedLast = ""
            kotlinFile.nameWithoutExtension to (mapOf(
                "kotlin" to try {
                    ExecuteFileTester.kotlin(kotlinFile, additionalSources = listOf(File("../android/src/main/java/com/lightningkite/khrysalis/Swift.kt")))
                        .also { expectedLast = it }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "FAILED TO COMPILE/EXECUTE: ${e.message}"
                }//,
//                "typescript" to try {
//                    ExecuteFileTester.typescript(typescriptFile)
//                    .also {
//                        if(expectedLast == it) {
//                            System.out.print(".")
//                        } else {
//                            System.out.print("F")
//                        }
//                    }
//                } catch(e:Exception){
                // e.printStackTrace()
//                    "FAILED TO COMPILE/EXECUTE"
//                }
            ) + if (isMac) {
                mapOf(
                    "swift" to try {
                        ExecuteFileTester.swift(swiftFile, listOf(File("../ios/Khrysalis/com/lightningkite/khrysalis/kotlin")))
                            .also {
                                if(expectedLast == it) {
                                    System.out.print(".")
                                } else {
                                    System.out.print("F")
                                }
                                System.out.flush()
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "FAILED TO COMPILE/EXECUTE: ${e.message}"
                    }
                )
            } else {
                mapOf()
            })
        }

        println(" Finished")

        val failures = results.entries.flatMap { result ->
            val expected = result.value["kotlin"]
            result.value
                .filter { it.value != expected }
                .map { "${result.key} failed to match Kotlin when converted to ${it.key.capitalize()}; expected '$expected', but got '${it.value}'" }
        }

        if (failures.isNotEmpty()) {
            println("${failures.size} subtests failed:")
            for (fail in failures) {
                println(fail)
            }
            throw Exception()
        }
    }
}

package com.lightningkite.khrysalis

import com.lightningkite.khrysalis.ios.swift.convertToSwift
import org.junit.Test
import java.io.File
import java.net.URL

class CodeConversionTest {

    /**
     * How do we set this up?
     *
     * What we want:
     * - Full use of Butterfly, ideally
     */

    @Test
    fun everything() {
//        val kotlinFiles = File("./testData/kotlin")
//        val swiftFiles = File("./testData/swift")
//        val typescriptFiles = File("./testData/typescript")
//
//        val clean = false
//
//        val isMac = System.getProperty("os.name").toLowerCase().let { it.contains("osx") || it.contains("mac") }
//
//        val standardLibraryCopy = File("build/temp/std-lib.jar").also { it.parentFile.mkdirs() }
//        if (!standardLibraryCopy.exists()) {
//            println("Downloading standard library...")
//            standardLibraryCopy.outputStream().use { out ->
//                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.3.72/kotlin-stdlib-1.3.72.jar").openStream()
//                    .use { input ->
//                        input.copyTo(out)
//                    }
//            }
//        }
//
//        if (isMac) {
//            convertToSwift(
//                interfacesOut = File("./testDataOutput/interfaces.json").also { it.parentFile.mkdirs() },
//                baseKotlin = File("./testData/shared"),
//                baseSwift = File("./testDataOutput/shared").also { it.mkdirs() },
//                clean = true,
//                setup = {
//                    this.imports = listOf()
//                }
//            )
//        }
//        convertToTypescript(
//            projectName = null,
//            libraries = sequenceOf(standardLibraryCopy),
//            files = File("./testData/shared").walkTopDown() + sequenceOf(File("../android/src/main/java/com/lightningkite/khrysalis/Swift.kt")),
//            output = File("./testDataOutputTs/shared").also { it.mkdirs() },
//            pluginCache = File("./build/tsTest/pluginCache"),
//            buildCache = File("./build/tsTest/pluginCache"),
//            dependencies = File("../kotlin-compiler-plugin-typescript/replacements").walkTopDown()
//        )
//
//        val results = kotlinFiles.walkTopDown().filter { it.name.endsWith(".shared.kt") }.associate { kotlinFile ->
//            val relative = kotlinFile.relativeTo(kotlinFiles)
//            val swiftFile = swiftFiles.resolve(relative).parentFile
//                .resolve(kotlinFile.nameWithoutExtension + ".swift")
//            val typescriptFile = typescriptFiles.resolve(relative).parentFile
//                .resolve(kotlinFile.nameWithoutExtension + ".ts")
//            var expectedLast = ""
//            kotlinFile.nameWithoutExtension to (mapOf(
//                "kotlin" to try {
//                    ExecuteFileTester.kotlin(
//                            kotlinFile,
//                            clean = clean,
//                            additionalSources = listOf(File("../android/src/main/java/com/lightningkite/khrysalis/Swift.kt"))
//                        )
//                        .also { expectedLast = it }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    "FAILED TO COMPILE/EXECUTE: ${e.message}"
//                },
//                "typescript" to try {
//                    ExecuteFileTester.typescript(typescriptFile, clean = clean)
//                        .also {
//                            if (expectedLast == it) {
//                                print(".")
//                            } else {
//                                print("F")
//                            }
//                            System.out.flush()
//                        }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    "FAILED TO COMPILE/EXECUTE"
//                }
//            ) + if (isMac) {
//                mapOf(
//                    "swift" to try {
//                        ExecuteFileTester.swift(
//                            swiftFile,
//                            clean = clean,
//                            directories = listOf(File("../ios/Khrysalis/com/lightningkite/khrysalis/kotlin"))
//                        ).also {
//                            if (expectedLast == it) {
//                                print(".")
//                            } else {
//                                print("F")
//                            }
//                            System.out.flush()
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        "FAILED TO COMPILE/EXECUTE: ${e.message}"
//                    }
//                )
//            } else {
//                mapOf()
//            })
//        }
//
//        println(" Finished")
//
//        val failures = results.entries.flatMap { result ->
//            val expected = result.value["kotlin"]
//            result.value
//                .filter { it.value != expected }
//                .map { "${result.key} failed to match Kotlin when converted to ${it.key.capitalize()}; expected '$expected', but got '${it.value}'" }
//        }
//
//        if (failures.isNotEmpty()) {
//            println("${failures.size} subtests failed:")
//            for (fail in failures.sorted()) {
//                println(fail)
//            }
//            throw Exception()
//        }
    }
}

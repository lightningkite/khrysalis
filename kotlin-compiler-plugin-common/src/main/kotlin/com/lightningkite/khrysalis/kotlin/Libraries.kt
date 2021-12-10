package com.lightningkite.khrysalis.kotlin

import java.io.File
import java.net.URL

object Libraries {
    fun getStandardLibrary(): File {
        val standardLibraryCopy = File("build/temp/std-lib-1-6-0.jar").also { it.parentFile.mkdirs() }
        if (!standardLibraryCopy.exists()) {
            println("Downloading standard library...")
            standardLibraryCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.0/kotlin-stdlib-1.6.0.jar").openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        return standardLibraryCopy
    }

    val khrysalisAnnotations = File("../jvm-runtime/src/main/java").walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
    val junitStubs = File("../conversionTestData/junitStubs.kt")
    val jacksonStubs = File("../conversionTestData/jacksonStubs.kt")

    val testingStubs = khrysalisAnnotations + listOf(junitStubs, jacksonStubs)
}
package com.lightningkite.khrysalis.kotlin

import java.io.File
import java.net.URL

object Libraries {
    fun getStandardLibrary(): File {
        val standardLibraryCopy = File("build/temp/std-lib-1-4-30.jar").also { it.parentFile.mkdirs() }
        if (!standardLibraryCopy.exists()) {
            println("Downloading standard library...")
            standardLibraryCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.4.30/kotlin-stdlib-1.4.30.jar").openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        return standardLibraryCopy
    }

    val khrysalisAnnotations = File(System.getenv("KHRYSALIS_META_LOCATION"))
        .resolve("butterfly-android/butterfly-android/src/main/java/com/lightningkite/butterfly/KhrysalisAnnotations.kt")
    val junitStubs = File(System.getenv("KHRYSALIS_META_LOCATION"))
        .resolve("khrysalis/kotlin-compiler-plugin-typescript/testData/junitStubs.kt")
    val jacksonStubs = File(System.getenv("KHRYSALIS_META_LOCATION"))
        .resolve("khrysalis/kotlin-compiler-plugin-typescript/testData/jacksonStubs.kt")

    val testingStubs = listOf(
        khrysalisAnnotations,
        junitStubs,
        jacksonStubs
    )
}
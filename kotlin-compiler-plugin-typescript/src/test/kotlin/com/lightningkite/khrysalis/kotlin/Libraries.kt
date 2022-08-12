package com.lightningkite.khrysalis.kotlin

import java.io.File
import java.net.URL

object Libraries {
    fun getStandardLibrary(): File {
        val standardLibraryCopy = File("build/temp/std-lib-1.7.10.jar").also { it.parentFile.mkdirs() }
        if (!standardLibraryCopy.exists()) {
            standardLibraryCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.7.10/kotlin-stdlib-1.7.10.jar").openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        return standardLibraryCopy.absoluteFile
    }
    fun getSerializationLibraryCore(): File {
        val serializationLibraryCopy = File("build/temp/std-ser-1-3-2.jar").also { it.parentFile.mkdirs() }
        if (!serializationLibraryCopy.exists()) {
            println("Downloading serialization library...")
            serializationLibraryCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-core-jvm/1.3.2/kotlinx-serialization-core-jvm-1.3.2.jar")
                    .openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        return serializationLibraryCopy.absoluteFile
    }
    fun getSerializationLibraryJson(): File {
        val serializationLibraryCopy = File("build/temp/std-ser-json-1.3.2.jar").also { it.parentFile.mkdirs() }
        if (!serializationLibraryCopy.exists()) {
            println("Downloading serialization library...")
            serializationLibraryCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-json-jvm/1.3.2/kotlinx-serialization-json-jvm-1.3.2.jar")
                    .openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        return serializationLibraryCopy.absoluteFile
    }
    fun getSerializationPlugin(): File {
        val serializationPluginCopy = File("build/temp/std-ser-plugin-1.7.10.jar").also { it.parentFile.mkdirs() }
        if (!serializationPluginCopy.exists()) {
            println("Downloading serialization library...")
            serializationPluginCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-serialization/1.7.10/kotlin-serialization-1.7.10.jar").openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        return serializationPluginCopy.absoluteFile
    }

    val khrysalisAnnotations = File("../jvm-runtime/src/main/java").walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
    val junitStubs = File("../conversionTestData/junitStubs.kt")
    val jacksonStubs = File("../conversionTestData/jacksonStubs.kt")

    val testingStubs = khrysalisAnnotations + listOf(junitStubs, jacksonStubs)
}
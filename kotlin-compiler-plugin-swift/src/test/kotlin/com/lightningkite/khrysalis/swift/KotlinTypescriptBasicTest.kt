package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import org.junit.Test
import java.io.File
import java.net.URL

class KotlinSwiftBasicTest {

    @Test
    fun test() {
        println("Running in ${File(".").absolutePath}")
        val standardLibraryCopy = File("build/temp/std-lib.jar").also { it.parentFile.mkdirs() }
        if (!standardLibraryCopy.exists()) {
            println("Downloading standard library...")
            standardLibraryCopy.outputStream().use { out ->
                URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.3.72/kotlin-stdlib-1.3.72.jar").openStream()
                    .use { input ->
                        input.copyTo(out)
                    }
            }
        }
        K2JVMCompiler().exec(
            messageCollector = object : MessageCollector {
                override fun clear() {

                }

                override fun hasErrors(): Boolean {
                    return false
                }

                override fun report(
                    severity: CompilerMessageSeverity,
                    message: String,
                    location: CompilerMessageLocation?
                ) {
                    if (message.isNotBlank())
                        println(message + if(location != null) ": $location" else "")
                }

            },
            services = Services.EMPTY,
            arguments = K2JVMCompilerArguments().apply {
                this.freeArgs = listOf("testData")
                this.classpathAsList = listOf(standardLibraryCopy)
                this.pluginClasspaths = arrayOf("build/libs/kotlin-compiler-plugin-swift-0.1.0.jar")
                this.pluginOptions =
                    arrayOf(
                        "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinSwiftCLP.KEY_TS_DEPENDENCIES_NAME}=testDataOut",
                        "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinSwiftCLP.KEY_OUTPUT_DIRECTORY_NAME}=testDataOut/swift"
                    )
                this.destinationAsFile = File("build/testBuild").also { it.deleteRecursively(); it.mkdirs() }
            }
        )
        println("Complete.")
    }
}

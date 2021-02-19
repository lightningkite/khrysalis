package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.kotlin.Libraries
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import org.junit.Test
import java.io.File
import java.net.URL

class KotlinTypescriptBasicTest {

    @Test
    fun test() {
        println("Running in ${File(".").absolutePath}")
        val standardLibraryCopy = Libraries.getStandardLibrary()
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
                    location: CompilerMessageSourceLocation?
                ) {
                    if (message.isNotBlank())
                        println(message + if(location != null) ": $location" else "")
                }

            },
            services = Services.EMPTY,
            arguments = K2JVMCompilerArguments().apply {
                this.freeArgs = listOf("oldTestData")
                this.classpathAsList = listOf(standardLibraryCopy)
                this.pluginClasspaths = arrayOf("build/libs/kotlin-compiler-plugin-typescript-0.1.0.jar")
                this.pluginOptions =
                    arrayOf(
                        "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTypescriptCLP.KEY_TS_DEPENDENCIES_NAME}=oldTestDataOut",
                        "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTypescriptCLP.KEY_OUTPUT_DIRECTORY_NAME}=oldTestDataOut/typescript"
                    )
                this.destinationAsFile = File("build/testBuild").also { it.deleteRecursively(); it.mkdirs() }
            }
        )
        println("Complete.")
    }
}

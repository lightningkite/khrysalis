package com.lightningkite.khrysalis.kotlin

import com.lightningkite.khrysalis.util.checksum
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import kotlin.math.absoluteValue

object ExecuteFileTester {
    val buildDir =
        System.getProperty("java.io.tmpdir").let { File(it) }.resolve("codeTranslationTesting").also { it.mkdirs() }
    val outCacheDir = File("build/testCompilationCache").also { it.mkdirs() }

    fun tempFile(sourceText: String): File {
        val file = File("build/testFiles/S${sourceText.hashCode().absoluteValue}.kt")
        file.parentFile.mkdirs()
        file.writeText(sourceText)
        return file
    }

    inline fun caching(sourceFile: File, clean: Boolean, action:()->String):String {
        val sourceFileChecksum = sourceFile.checksum()
        val cacheFile =
            outCacheDir.resolve(
                sourceFile.absolutePath.substringAfter("khrysalis").filter { it.isLetterOrDigit() } + ".out")
        cacheFile.parentFile.mkdirs()
        if (!clean && cacheFile.exists() && cacheFile.useLines { it.first() == sourceFileChecksum }) {
            return cacheFile.readText().substringAfter('\n').trim()
        }

        val result = action()
        cacheFile.writeText(sourceFileChecksum + "\n" + result)
        return result
    }

    fun kotlinCompile(
        sourceFile: File,
        libraries: Set<File> = setOf(Libraries.getStandardLibrary()),
        additionalSources: List<File> = Libraries.testingStubs,
        argumentsModification: K2JVMCompilerArguments.()->Unit,
        out: File = buildDir.resolve(sourceFile.nameWithoutExtension + ".jar")
    ) {
        val failures = ArrayList<String>()
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
                    if(severity <= CompilerMessageSeverity.ERROR) {
                        failures.add(message + if(location != null) ": $location" else "")
                    }
                }

            },
            services = Services.EMPTY,
            arguments = K2JVMCompilerArguments().apply {
                this.useIR = true
                this.freeArgs = (listOf(sourceFile) + additionalSources).map { it.path }.also { println(it) }
                this.classpathAsList = libraries.toList()
                this.destinationAsFile = out
            }.apply(argumentsModification)
        )
        if(failures.isNotEmpty()){
            throw Exception(failures.joinToString("\n"))
        }
    }

    fun kotlin(
        sourceFile: File,
        clean: Boolean = false,
        libraries: Set<File> = setOf(Libraries.getStandardLibrary()),
        additionalSources: List<File> = Libraries.testingStubs
    ): String = caching(sourceFile, clean) {
        val outFile = buildDir.resolve(sourceFile.nameWithoutExtension + ".jar")
        val ktName = sourceFile.name
            .split('.')
            .joinToString("") { it.filter { it.isJavaIdentifierPart() }.capitalize() }
        println(ktName)
        val packageName: String =
            sourceFile.useLines { it.find { it.trim().startsWith("package") }?.substringAfter("package ")?.trim() }
                ?: ""
        kotlinCompile(sourceFile, libraries, additionalSources, {}, outFile)

        captureSystemOut {
            JVM.runMain(libraries.toList() + outFile, if(packageName.isNotEmpty()) "$packageName.$ktName" else ktName, arrayOf<String>())
        }.trim()
    }
}

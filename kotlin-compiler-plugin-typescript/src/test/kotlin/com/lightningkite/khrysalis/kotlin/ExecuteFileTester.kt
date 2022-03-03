package com.lightningkite.khrysalis.kotlin

import com.lightningkite.khrysalis.util.checksum
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
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

    fun tempFile(sourceText: String): File {
        val file = File("build/testFiles/S${sourceText.hashCode().absoluteValue}.kt")
        file.parentFile.mkdirs()
        file.writeText(sourceText)
        return file
    }

    fun kotlin(
        sourceFile: File,
        compilationSetup: KotlinCompilation.()->Unit = {}
    ): String {
        val libraries = listOf(Libraries.getStandardLibrary(), Libraries.getSerializationLibraryCore(), Libraries.getSerializationLibraryJson())
        val ktName = sourceFile.name
            .split('.')
            .joinToString("") { it.filter { it.isJavaIdentifierPart() }.capitalize() }
        val packageName: String =
            sourceFile.useLines { it.find { it.trim().startsWith("package") }?.substringAfter("package ")?.trim() }
                ?: ""
        val outFile = KotlinCompilation().apply {
            sources = listOf(SourceFile.fromPath(sourceFile)) + Libraries.testingStubs.map { SourceFile.fromPath(it) }
            classpaths += libraries
            inheritClassPath = true
            pluginClasspaths += Libraries.getSerializationPlugin()
            compilationSetup()
            println("classpaths: $classpaths")
            println("pluginClasspaths: $pluginClasspaths")
        }.compile().outputDirectory

        return captureSystemOut {
            JVM.runMain(libraries + listOf(outFile), if(packageName.isNotEmpty()) "$packageName.$ktName" else ktName, arrayOf<String>())
        }.trim()
    }
}

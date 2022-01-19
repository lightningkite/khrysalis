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

    fun kotlin(
        sourceFile: File,
        clean: Boolean = false
    ): String = caching(sourceFile, clean) {
        val ktName = sourceFile.name
            .split('.')
            .joinToString("") { it.filter { it.isJavaIdentifierPart() }.capitalize() }
        println(ktName)
        val packageName: String =
            sourceFile.useLines { it.find { it.trim().startsWith("package") }?.substringAfter("package ")?.trim() }
                ?: ""
        val outFile = KotlinCompilation().apply {
            sources = listOf(SourceFile.fromPath(sourceFile)) + Libraries.testingStubs.map { SourceFile.fromPath(it) }
            inheritClassPath = true
        }.compile().outputDirectory

        println(listOf(Libraries.getStandardLibrary(), outFile))
        captureSystemOut {
            JVM.runMain(listOf(Libraries.getStandardLibrary(), outFile), if(packageName.isNotEmpty()) "$packageName.$ktName" else ktName, arrayOf<String>())
        }.trim()
    }
}

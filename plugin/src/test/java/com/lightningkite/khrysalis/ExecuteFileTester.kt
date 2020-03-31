package com.lightningkite.khrysalis

import com.lightningkite.khrysalis.utils.checksum
import java.io.File

object ExecuteFileTester {
    val buildDir =
        System.getProperty("java.io.tmpdir").let { File(it) }.resolve("codeTranslationTesting").also { it.mkdirs() }
    val outCacheDir = File("build/testCompilationCache").also { it.mkdirs() }

    fun swift(sourceFile: File, directories: List<File> = listOf()): String {
        val sourceFileChecksum = sourceFile.checksum()
        val cacheFile =
            outCacheDir.resolve(
                sourceFile.absolutePath.substringAfter("khrysalis").filter { it.isLetterOrDigit() } + ".out")
        if (cacheFile.exists() && cacheFile.useLines { it.first() == sourceFileChecksum }) {
            return cacheFile.readText().substringAfter('\n').trim()
        }
        val copyFile = buildDir.resolve("main.swift")
        val outFile = buildDir.resolve(sourceFile.nameWithoutExtension)
        val outputFile = buildDir.resolve(sourceFile.nameWithoutExtension + ".out")
        copyFile.writeText(sourceFile.readText() + "\n" + "main()")
        if (0 == ProcessBuilder()
                .command(
                    listOf(
                        "swiftc",
                        copyFile.absolutePath
                    ) + directories.flatMap {
                        if (it.extension == "swift")
                            listOf(it.absolutePath)
                        else it.walkTopDown()
                            .filter { it.extension == "swift" }
                            .map { it.absolutePath }
                            .toList()
                    } + listOf("-o", outFile.absolutePath))
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
                .waitFor()
        ) {
            ProcessBuilder()
                .command(outFile.path)
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
                .waitFor()
        }
        val result = outputFile.readText().trim()
        cacheFile.writeText(sourceFileChecksum + "\n" + result)
        return result
    }

    fun kotlin(sourceFile: File, libraries: List<File> = listOf(), additionalSources: List<File> = listOf()): String {
        val sourceFileChecksum = sourceFile.checksum()
        val cacheFile =
            outCacheDir.resolve(
                sourceFile.absolutePath.substringAfter("khrysalis").filter { it.isLetterOrDigit() } + ".out")
        if (cacheFile.exists() && cacheFile.useLines { it.first() == sourceFileChecksum }) {
            return cacheFile.readText().substringAfter('\n').trim()
        }
        val copyFile = buildDir.resolve(sourceFile.name)
        val outFile = buildDir.resolve(sourceFile.nameWithoutExtension + ".jar")
        val outputFile = buildDir.resolve(sourceFile.nameWithoutExtension + ".out")
        val packageName: String =
            sourceFile.useLines { it.find { it.trim().startsWith("package") }?.substringAfter("package ")?.trim() }
                ?: ""
        copyFile.writeText("""@file:JvmName("MainKt")""" + "\n" + sourceFile.readText())
        if (0 == ProcessBuilder()
                .command(listOf("kotlinc", copyFile.path) + additionalSources.map { it.path } + listOf("-classpath") +
                        (listOf(outFile) + libraries).joinToString(File.pathSeparator) { it.path } +
                        listOf(
                            "-d",
                            outFile.path
                        ))
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
                .waitFor()
        ) {
            ProcessBuilder()
                .command(
                    "kotlin",
                    "-classpath",
                    (listOf(outFile) + libraries).joinToString(File.pathSeparator) { it.path },
                    if (packageName.isEmpty()) "MainKt" else "$packageName.MainKt"
                )
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
                .waitFor()
        }
        val result = outputFile.readText().trim()
        cacheFile.writeText(sourceFileChecksum + "\n" + result)
        return result
    }

    fun typescript(sourceFile: File): String {
        val sourceFileChecksum = sourceFile.checksum()
        val cacheFile =
            outCacheDir.resolve(
                sourceFile.absolutePath.substringAfter("khrysalis").filter { it.isLetterOrDigit() } + ".out")
        if (cacheFile.exists() && cacheFile.useLines { it.first() == sourceFileChecksum }) {
            return cacheFile.readText().substringAfter('\n').trim()
        }
        val copyFile = buildDir.resolve(sourceFile.name)
        val outFile = buildDir.resolve(sourceFile.nameWithoutExtension + ".js")
        val outputFile = buildDir.resolve(sourceFile.nameWithoutExtension + ".out")
        copyFile.writeText(sourceFile.readText() + "\n" + "main()")
        if (0 == ProcessBuilder()
                .command("tsc", "--outFile", outFile.path, copyFile.path)
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
                .waitFor()
        ) {
            ProcessBuilder()
                .command("node", outFile.path)
                .redirectErrorStream(true)
                .redirectOutput(outputFile)
                .start()
                .waitFor()
        }
        val result = outputFile.readText().trim()
        cacheFile.writeText(sourceFileChecksum + "\n" + result)
        return result
    }
}

package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import com.lightningkite.khrysalis.kotlin.Libraries
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import java.io.File

val tsTestDir = File("./testOut")
fun ExecuteFileTester.ts(sourceFile: File, clean: Boolean): String = caching(sourceFile, clean) {
    val mainFile = tsTestDir.resolve("src/index.ts")
    val outputFile = tsTestDir.resolve("build").resolve(sourceFile.nameWithoutExtension + ".out")
    outputFile.parentFile.mkdirs()

    mainFile.writeText("import { main } from \"./${sourceFile.nameWithoutExtension}\"\nmain()")
    if (0 == ProcessBuilder()
            .directory(tsTestDir)
            .command("tsc")
            .redirectErrorStream(true)
            .redirectOutput(outputFile)
            .start()
            .waitFor()
    ) {
        ProcessBuilder()
            .directory(tsTestDir.resolve("dist"))
            .command("node", "index.js")
            .redirectErrorStream(true)
            .redirectOutput(outputFile)
            .start()
            .waitFor()
    } else {
        throw Exception("Typescript compilation failed: ${outputFile.readText().trim()}")
    }

    outputFile.readText().trim()
}

fun ExecuteFileTester.tsTranslated(file: File): String {
    tsTestDir.resolve("src").listFiles()!!.forEach { it.deleteRecursively() }
    return ts(compileToTs(file), true)
}

fun ExecuteFileTester.compileToTs(file: File): File {
    val outFolder = tsTestDir.resolve("src")
    this.kotlinCompile(
        sourceFile = file,
        argumentsModification = {
            this.pluginClasspaths = arrayOf("build/libs/kotlin-compiler-plugin-typescript-0.1.0.jar")
            this.pluginOptions =
                arrayOf(
                    "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTypescriptCLP.KEY_TS_DEPENDENCIES_NAME}=${outFolder.parentFile}",
                    "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTypescriptCLP.KEY_OUTPUT_DIRECTORY_NAME}=${outFolder}"
                )
        }
    )
    return outFolder.resolve(file.nameWithoutExtension + ".ts")
}

fun Libraries.translationFilesButterfly(): File {
    return File(System.getenv("KHRYSALIS_META_LOCATION"))
        .resolve("butterfly-web")
}
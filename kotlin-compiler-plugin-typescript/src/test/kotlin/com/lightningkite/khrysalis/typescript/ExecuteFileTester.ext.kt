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
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.util.correctedFileOutput

private var preparedForTest: Boolean = false
private fun prepareForTest() {
    if(preparedForTest) return

    if (!tsTestDir.resolve("node_modules").exists()) {
        ProcessBuilder()
            .directory(tsTestDir)
            .command("npm", "install")
            .inheritIO()
            .start()
            .waitFor()
    }

    // Copy library
    val libraryFolder = tsTestDir.resolve("../../web-runtime")
    ProcessBuilder()
        .directory(libraryFolder)
        .command("npm", "install")
        .inheritIO()
        .start()
        .waitFor()
    ProcessBuilder()
        .directory(libraryFolder)
        .command("npm", "run", "build")
        .inheritIO()
        .start()
        .waitFor()
    libraryFolder.resolve("dist").copyRecursively(tsTestDir.resolve("node_modules/khrysalis-runtime/dist"), overwrite = true)
    libraryFolder.resolve("src").copyRecursively(tsTestDir.resolve("node_modules/khrysalis-runtime/src"), overwrite = true)
    libraryFolder.resolve("index.js").copyTo(tsTestDir.resolve("node_modules/khrysalis-runtime/index.js"), overwrite = true)
    libraryFolder.resolve("index.d.ts").copyTo(tsTestDir.resolve("node_modules/khrysalis-runtime/index.d.ts"), overwrite = true)
    libraryFolder.resolve("package.json").copyTo(tsTestDir.resolve("node_modules/khrysalis-runtime/package.json"), overwrite = true)
    libraryFolder.resolve("tsconfig.json").copyTo(tsTestDir.resolve("node_modules/khrysalis-runtime/tsconfig.json"), overwrite = true)

    preparedForTest = true
}

val tsTestDir = File("./testOut")
fun ExecuteFileTester.ts(sourceFile: File, clean: Boolean): String = caching(sourceFile, clean) {
    val mainFile = tsTestDir.resolve("src/main.ts")
    val outputFile = tsTestDir.resolve("build").resolve(sourceFile.nameWithoutExtension + ".out")
    outputFile.parentFile.mkdirs()

    println("Compiling ${tsTestDir.absolutePath}")
    println(sourceFile.readText())

    mainFile.writeText("import { main } from \"./${sourceFile.nameWithoutExtension}\"\nmain()")
    if (0 == ProcessBuilder()
            .directory(tsTestDir)
            .command("npm", "run", "build")
            .redirectErrorStream(true)
            .start()
            .correctedFileOutput(outputFile)
            .waitFor()
    ) {
        ProcessBuilder()
            .directory(tsTestDir)
            .command("node", "dist/main.js")
            .redirectErrorStream(true)
            .start()
            .correctedFileOutput(outputFile)
            .waitFor()
    } else {
        throw Exception("Typescript compilation failed: ${outputFile.readText().trim()}")
    }
//    ProcessBuilder()
//        .directory(tsTestDir)
//        .command("npm", "run", "start")
//        .redirectErrorStream(true)
//        .start()
//        .correctedFileOutput(outputFile)
//        .waitFor()

    return outputFile.readText().trim()
}

fun ExecuteFileTester.tsTranslated(file: File): String {
    tsTestDir.resolve("src").listFiles()?.forEach { it.deleteRecursively() }
    prepareForTest()
    return ts(compileToTs(file), true)
}

fun ExecuteFileTester.compileToTs(file: File): File {
    val outFolder = tsTestDir.resolve("src")
    this.kotlinCompile(
        sourceFile = file,
        argumentsModification = {
            this.pluginClasspaths = arrayOf("build/libs/kotlin-compiler-plugin-typescript-0.7.1.jar")
            this.pluginOptions =
                arrayOf(
                    "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_EQUIVALENTS_NAME}=${tsTestDir}",
                    "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME}=${outFolder}",
                    "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_PROJECT_NAME_NAME}=Yeet"
                )
        }
    )
    return outFolder.resolve(file.nameWithoutExtension + ".ts")
}

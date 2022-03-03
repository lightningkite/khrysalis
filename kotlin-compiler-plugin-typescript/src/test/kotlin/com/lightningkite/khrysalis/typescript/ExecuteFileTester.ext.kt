package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import com.lightningkite.khrysalis.util.readInto
import com.tschuchort.compiletesting.PluginOption
import java.io.File

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
    val libraryFolder = tsTestDir.resolve("../..")
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
    tsTestDir.resolve("node_modules/@lightningkite/khrysalis-runtime").mkdirs()
    libraryFolder.resolve("web-runtime").copyRecursively(tsTestDir.resolve("node_modules/@lightningkite/khrysalis-runtime/web-runtime"), overwrite = true)
    libraryFolder.resolve("index.js").copyTo(tsTestDir.resolve("node_modules/@lightningkite/khrysalis-runtime/index.js"), overwrite = true)
    libraryFolder.resolve("index.d.ts").copyTo(tsTestDir.resolve("node_modules/@lightningkite/khrysalis-runtime/index.d.ts"), overwrite = true)
    libraryFolder.resolve("package.json").copyTo(tsTestDir.resolve("node_modules/@lightningkite/khrysalis-runtime/package.json"), overwrite = true)
    libraryFolder.resolve("tsconfig.json").copyTo(tsTestDir.resolve("node_modules/@lightningkite/khrysalis-runtime/tsconfig.json"), overwrite = true)
    preparedForTest = true
}

val tsTestDir = File("./testOut").absoluteFile.also { println("testOut = ${it}") }
fun ExecuteFileTester.ts(sourceFile: File): String {
    val mainFile = tsTestDir.resolve("src/main.ts")
    val outputFile = tsTestDir.resolve("build").resolve(sourceFile.nameWithoutExtension + ".out")
    outputFile.parentFile.mkdirs()

    println("Compiling ${tsTestDir.absolutePath}")

    mainFile.writeText("import { main } from \"./${sourceFile.nameWithoutExtension}\"\nmain()")

    var output: String = ""
    ProcessBuilder()
        .directory(tsTestDir)
        .command("npm", "run", "start")
        .redirectErrorStream(true)
        .start()
        .readInto { output = it }
        .waitFor()

    return output.substringAfter("> ts-node src/main.ts").trim()
}
data class Results(val kotlin: String, val typescript: String)

fun ExecuteFileTester.tsTranslated(file: File): Results {
    tsTestDir.resolve("src").listFiles()?.forEach { it.deleteRecursively() }
    prepareForTest()
    val kat = kotlinAndTs(file)
    return Results(kat.kotlinRunResult, ts(kat.typescriptFile))
}
data class KatResult(val kotlinRunResult: String, val typescriptFile: File)

fun ExecuteFileTester.kotlinAndTs(file: File): KatResult {
    val outFolder = tsTestDir.resolve("src")
    val kResult = kotlin(file) {
        commandLineProcessors = listOf(KotlinTypescriptCLP())
        compilerPlugins += listOf(KotlinTypescriptCR())
        pluginOptions += listOf(
            PluginOption(KotlinTypescriptCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_EQUIVALENTS_NAME, tsTestDir.toString()),
            PluginOption(KotlinTypescriptCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME, outFolder.toString()),
            PluginOption(KotlinTypescriptCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_COMMON_PACKAGE_NAME, file.readText().substringAfter("package ").substringBefore('\n').trim()),
            PluginOption(KotlinTypescriptCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_PROJECT_NAME_NAME, "Yeet"),
            PluginOption(KotlinTypescriptCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_LIBRARY_MODE_NAME, "false"),
        )
    }
    return KatResult(kResult, outFolder.resolve(file.nameWithoutExtension + ".ts"))
}

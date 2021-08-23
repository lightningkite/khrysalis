package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import com.lightningkite.khrysalis.kotlin.Libraries
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import java.io.File

val swiftTestDir = File("./testOut")
fun ExecuteFileTester.swift(sourceFile: File, clean: Boolean): String = caching(sourceFile, clean) {
    val mainFile = swiftTestDir.resolve("Sources/testOut/main.swift")
    mainFile.writeText("print(\"BEGIN PROGRAM\")\nmain()")
    val outputFile = swiftTestDir.resolve("build").resolve(sourceFile.nameWithoutExtension + ".out")
    outputFile.parentFile.mkdirs()

    ProcessBuilder("swift", "run")
        .directory(swiftTestDir)
        .redirectErrorStream(true)
        .redirectOutput(outputFile)
        .start()
        .waitFor()

    outputFile.readText().substringAfter("BEGIN PROGRAM").trim()
}

fun ExecuteFileTester.swiftTranslated(file: File): String {
    swiftTestDir.resolve("Sources/testOut").also { it.mkdirs() }.listFiles()!!.forEach { it.deleteRecursively() }
    return swift(compileToSwift(file), true)
}

fun ExecuteFileTester.compileToSwift(file: File): File {
    val outFolder = swiftTestDir.resolve("Sources/testOut")
    this.kotlinCompile(
        sourceFile = file,
        argumentsModification = {
            this.pluginClasspaths = arrayOf("build/libs/kotlin-compiler-plugin-swift-0.1.0.jar")
            this.pluginOptions =
                arrayOf(
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_EQUIVALENTS_NAME}=${Libraries.translationFilesButterfly()}",
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME}=${outFolder}",
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_PROJECT_NAME_NAME}=Yeet"
                )
        }
    )
    return outFolder.resolve(file.nameWithoutExtension + ".swift")
}

fun Libraries.translationFilesButterfly(): File {
    return File(System.getenv("KHRYSALIS_META_LOCATION"))
        .resolve("butterfly-ios")
}
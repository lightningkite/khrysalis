package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import com.lightningkite.khrysalis.kotlin.Libraries
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.util.readInto
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

val swiftTestDir = File("./testOut")
fun ExecuteFileTester.swift(sourceFile: File, clean: Boolean): String = caching(sourceFile, clean) {

    //Copy libraries
    run {
        val src = File("../ios-runtime/KhrysalisRuntime/Classes")
        val dest = swiftTestDir.resolve("Sources/KhrysalisRuntime")
        if(!dest.exists()) {
            dest.mkdirs()
            src.listFiles()!!.forEach {
                it.copyRecursively(dest.resolve(it.relativeTo(src)), overwrite = true)
            }
            dest.resolve("core/UIColor.ext.swift").delete()
            dest.resolve("android/CGRect+bounds.swift").delete()
        }
    }

    val mainFile = swiftTestDir.resolve("Sources/testOut/main.swift")
    mainFile.writeText("print(\"BEGIN PROGRAM\")\nmain()")
    val outputFile = swiftTestDir.resolve("build").resolve(sourceFile.nameWithoutExtension + ".out")
    outputFile.parentFile.mkdirs()

    var output: String = ""
    ProcessBuilder("swift", "run")
        .directory(swiftTestDir)
        .redirectErrorStream(true)
        .start()
        .readInto { output = it }
        .waitFor()

    return output.substringAfter("BEGIN PROGRAM").trim()
}

fun ExecuteFileTester.swiftTranslated(file: File): String {
    swiftTestDir.resolve("Sources/testOut").also { it.mkdirs() }.listFiles()!!.forEach { it.deleteRecursively() }
    return swift(compileToSwift(file), true)
}

fun ExecuteFileTester.compileToSwift(file: File): File {
    val outFolder = swiftTestDir.resolve("Sources/testOut")
    KotlinCompilation().apply {
        inheritClassPath = true
        sources = listOf(SourceFile.fromPath(file)) + Libraries.testingStubs.map { SourceFile.fromPath(it) }
        commandLineProcessors = listOf(KotlinSwiftCLP())
        compilerPlugins = listOf(KotlinSwiftCR())
        pluginOptions = listOf(
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_EQUIVALENTS_NAME, swiftTestDir.resolve("Sources/KhrysalisRuntime").toString()),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME, outFolder.toString()),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_PROJECT_NAME_NAME, "Yeet"),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_LIBRARY_MODE_NAME, "false"),
        )
    }.compile()
    return outFolder.resolve(file.nameWithoutExtension + ".swift")
}

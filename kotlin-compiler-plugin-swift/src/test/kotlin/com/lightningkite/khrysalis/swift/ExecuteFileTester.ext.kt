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

private var ready = false
private fun makeReady() {
    if(ready) return

    //Copy libraries
    val src = File("../ios-runtime/KhrysalisRuntime/Classes")
    val dest = swiftTestDir.resolve("Sources/KhrysalisRuntime")
    dest.mkdirs()
    src.listFiles()!!.forEach {
        it.copyRecursively(dest.resolve(it.relativeTo(src)), overwrite = true)
    }
    dest.resolve("core/UIColor.ext.swift").delete()
    dest.resolve("android/CGRect+bounds.swift").delete()
    dest.resolve("core/Foundation.ext.swift").delete()

    byteArrayOf(1) + byteArrayOf(2)

    ready = true
}

val hasSwift: Boolean by lazy {
    try {
        ProcessBuilder("swift", "--version")
            .start()
            .waitFor()
        true
    } catch(e: Exception) {
        false
    }
}

val swiftTestDir = File("./testOut")
fun ExecuteFileTester.swift(sourceFile: File): String {
    makeReady()
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

data class Results(val kotlin: String, val swift: String)

fun ExecuteFileTester.swiftTranslated(file: File): Results {
    swiftTestDir.resolve("Sources/testOut").also { it.mkdirs() }.listFiles()!!.forEach { it.deleteRecursively() }
    val x = kotlinAndSwift(file)
    return Results(x.kotlinRunResult, swift(x.swiftFile))
}

data class KasResult(val kotlinRunResult: String, val swiftFile: File)

fun ExecuteFileTester.kotlinAndSwift(file: File): KasResult {
    makeReady()
    val outFolder = swiftTestDir.resolve("Sources/testOut")
    val kotlinOut = kotlin(file) {
        commandLineProcessors = listOf(KotlinSwiftCLP())
        compilerPlugins += listOf(KotlinSwiftCR())
        pluginOptions += listOf(
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_EQUIVALENTS_NAME, listOf(File("../jvm-runtime/src"), swiftTestDir.resolve("testReplacements")).joinToString(File.pathSeparator)),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME, outFolder.toString()),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_COMMON_PACKAGE_NAME, file.readText().substringAfter("package ").substringBefore('\n').trim()),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_PROJECT_NAME_NAME, "Yeet"),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_LIBRARY_MODE_NAME, "false"),
            PluginOption(KotlinSwiftCLP.PLUGIN_ID, KotlinTranspileCLP.KEY_OUTPUT_FQNAMES_NAME, outFolder.resolve("../ts.fqnames").toString()),
        )
    }
    return KasResult(kotlinOut, outFolder.resolve(file.nameWithoutExtension + ".swift"))
}

package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.kotlin.ExecuteFileTester
import com.lightningkite.khrysalis.kotlin.Libraries
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

fun swiftInstallation(): File? {

    val raw = System.getProperty("os.name").toLowerCaseAsciiOnly()
    return when {
        raw.contains("win") -> null  // TODO
        raw.contains("mac") || raw.contains("linux") -> {
            //Use local installation first
            val tempFile = File.createTempFile("swiftloc", ".txt")
            ProcessBuilder().command("which", "swift")
                .redirectOutput(tempFile)
                .start()
                .waitFor()
            tempFile.readText().trim().takeUnless { it.isEmpty() }?.let { File(it) }
        }
        else -> null
    }
}

val swiftTestDir = File("./testOut")
fun ExecuteFileTester.swift(sourceFile: File, clean: Boolean): String = caching(sourceFile, clean) {

    //Check swift is installed
    val swiftExe = swiftInstallation() ?: throw IllegalStateException("Swift not installed")

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

    ProcessBuilder(swiftExe.absolutePath, "run")
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
            this.pluginClasspaths = arrayOf("build/libs/kotlin-compiler-plugin-swift-0.2.0.jar")
            this.pluginOptions =
                arrayOf(
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_EQUIVALENTS_NAME}=${swiftTestDir.resolve("Sources/KhrysalisRuntime")}",
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME}=${outFolder}",
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_PROJECT_NAME_NAME}=Yeet",
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_LIBRARY_MODE_NAME}=false",
                )
        }
    )
    return outFolder.resolve(file.nameWithoutExtension + ".swift")
}

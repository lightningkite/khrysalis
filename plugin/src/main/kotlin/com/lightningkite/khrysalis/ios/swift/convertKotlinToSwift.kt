package com.lightningkite.khrysalis.ios.swift

import com.lightningkite.khrysalis.swift.KotlinSwiftCLP
import com.lightningkite.khrysalis.utils.copyFolderOutFromRes
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import java.io.File

fun convertToSwift(
    projectName: String? = null,
    libraries: Sequence<File>,
    files: Sequence<File>,
    pluginCache: File,
    buildCache: File,
    dependencies: Sequence<File>,
    output: File
){
    val result = K2JVMCompiler().exec(
        messageCollector = object : MessageCollector {
            override fun clear() {

            }

            override fun hasErrors(): Boolean {
                return false
            }

            override fun report(
                severity: CompilerMessageSeverity,
                message: String,
                location: CompilerMessageSourceLocation?
            ) {
                if (message.isNotBlank()/* && severity <= CompilerMessageSeverity.STRONG_WARNING*/) {
                    println(message + ":")
                    location?.toString()?.let { println(it) }
                }
            }

        },
        services = Services.EMPTY,
        arguments = K2JVMCompilerArguments().apply {
            this.freeArgs = files.filter { it.extension in setOf("kt", "java") }.map { it.absolutePath }.toList()
            this.classpathAsList = libraries.toList()
            this.pluginClasspaths = pluginCache.resolve("swift.jar")
                .also { it.parentFile.mkdirs() }
                .also {
                    copyFolderOutFromRes("compiler-plugins", it.parentFile)
                }
                .let { arrayOf(it.path) }
            this.pluginOptions =
                listOfNotNull(
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinSwiftCLP.KEY_OUTPUT_DIRECTORY_NAME}=\"${output.path}\"",
                    projectName?.let { "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinSwiftCLP.KEY_PROJECT_NAME_NAME}=\"${it}\"" },
                    "plugin:${KotlinSwiftCLP.PLUGIN_ID}:${KotlinSwiftCLP.KEY_DEPENDENCIES_NAME}=\"${dependencies.joinToString(File.pathSeparator)}\""
                ).toTypedArray()
            this.destinationAsFile = buildCache.also { it.mkdirs() }
        }
    )
    if (result.code != 0) {
        throw IllegalStateException("Got a code ${result.code} back from the compiler! ${result.name}")
    }
}
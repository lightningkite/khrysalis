package com.lightningkite.khrysalis.web

import com.lightningkite.khrysalis.utils.copyFolderOutFromRes
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import java.io.File

fun convertToTypescript(
    libraries: Sequence<File>,
    files: Sequence<File>,
    pluginCache: File,
    buildCache: File,
    equivalents: Sequence<File>,
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
                location: CompilerMessageLocation?
            ) {
                if (message.isNotBlank() && severity <= CompilerMessageSeverity.STRONG_WARNING) {
                    println(message + ":")
                    location?.toString()?.let { println(it) }
                }
            }

        },
        services = Services.EMPTY,
        arguments = K2JVMCompilerArguments().apply {
            this.freeArgs = files.filter { it.extension in setOf("kt", "java") }.map { it.absolutePath }.toList()
            this.classpathAsList = libraries.toList()
            this.pluginClasspaths = pluginCache.resolve("typescript.jar")
                .also { it.parentFile.mkdirs() }
                .also {
                    copyFolderOutFromRes("compiler-plugins", it.parentFile)
                }
                .let { arrayOf(it.path) }
            this.pluginOptions =
                arrayOf(
                    "plugin:com.lightningkite.khrysalis.typescript:outputDirectory=\"${output.path}\"",
                    "plugin:com.lightningkite.khrysalis.typescript:equivalents=\"${equivalents.joinToString(File.pathSeparator)}\""
                )
            this.destinationAsFile = buildCache.also { it.mkdirs() }
        }
    )
    if (result.code != 0) {
        throw IllegalStateException("Got a code ${result.code} back from the compiler! ${result.name}")
    }
}
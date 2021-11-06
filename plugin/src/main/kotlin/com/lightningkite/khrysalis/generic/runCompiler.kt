package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.utils.copyFolderOutFromRes
import org.gradle.api.Project
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import java.io.File

data class CompilerRunInfo(
    val libraries: Sequence<File>,
    val files: Sequence<File>,
    val buildCache: File,
) {
    constructor(compileTask: KotlinCompile):this(
        compileTask.classpath.asSequence(),
        compileTask.source.toList().asSequence(),
        compileTask.project.buildDir.resolve("testBuild")
    )
}
data class CompilerPluginUseInfo(
    val classpath: List<File>,
    val options: List<String>
) {
    constructor(cacheName: String, project: Project, options: List<String>):this(
        classpath = project.buildDir.resolve("khrysalis-kcp").resolve(cacheName)
            .also { it.parentFile.mkdirs() }
            .also {
                copyFolderOutFromRes("compiler-plugins", it.parentFile)
            }
            .let { listOf(it) },
        options = options
    )
}

fun runCompiler(
    compileInfo: CompilerRunInfo,
    pluginInfo: CompilerPluginUseInfo,
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
            this.useIR = true
            this.freeArgs = compileInfo.files.filter { it.extension in setOf("kt", "java") }.map { it.absolutePath }.toList()
            this.classpathAsList = compileInfo.libraries.toList()
            this.pluginClasspaths = pluginInfo.classpath.map { it.path }.toTypedArray()
            this.pluginOptions = pluginInfo.options.toTypedArray()
            this.destinationAsFile = compileInfo.buildCache.also { it.mkdirs() }
        }
    )
    if (result.code != 0) {
        throw IllegalStateException("Got a code ${result.code} back from the compiler! ${result.name}")
    }
}

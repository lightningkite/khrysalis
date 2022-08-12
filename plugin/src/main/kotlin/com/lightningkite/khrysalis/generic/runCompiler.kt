package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.gradle.forcePluginOptions
import com.lightningkite.khrysalis.gradle.plugin
import com.lightningkite.khrysalis.utils.copyFolderOutFromRes
import org.gradle.api.Project
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.gradle.tasks.CompilerPluginOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.incremental.classpathAsList
import org.jetbrains.kotlin.incremental.destinationAsFile
import java.io.File
import java.util.zip.ZipFile

data class CompilerRunInfo(
    val libraries: Sequence<File>,
    val files: Sequence<File>,
    val buildCache: File,
    val compilerPlugins: Sequence<File>,
    val pluginOptions: CompilerPluginOptions
) {
    constructor(compileTask: KotlinCompile):this(
        compileTask.classpath.asSequence(),
        compileTask.sources.files.toList().asSequence(),
        compileTask.project.buildDir.resolve("testBuild"),
        compileTask.pluginClasspath.asSequence(),
        compileTask.forcePluginOptions
    ) {

    }
}
data class CompilerPluginUseInfo(
    val classpath: List<File>,
    val options: List<String>
) {
    companion object {
        fun make(project: Project, pluginName: String, options: Map<String, String>): CompilerPluginUseInfo {
            val allRootDeps = project.configurations.maybeCreate("kcp")
                .resolvedConfiguration
                .firstLevelModuleDependencies
            val rootDep = allRootDeps
                .find { it.moduleName.contains(pluginName, true) }
                ?: throw IllegalStateException("No plugin found with name '$pluginName' in it.  Available: ${allRootDeps.joinToString { it.name }}")
            val pluginRawJar = rootDep.moduleArtifacts.find { it.classifier.isNullOrEmpty() && it.extension == "jar" }
                ?: throw IllegalStateException("Could not find JAR for '${rootDep.moduleName}'")
            val pluginId = ZipFile(pluginRawJar.file).use { zipFile ->
                zipFile.getEntry("META-INF/services/com.lightningkite.kotlin.kcpgradle.PluginName")?.let {
                    zipFile.getInputStream(it).readAllBytes().toString(Charsets.UTF_8)
                } ?: zipFile.getEntry("META-INF/services/org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor")
                    ?.let {
                        zipFile.getInputStream(it).readAllBytes().toString(Charsets.UTF_8).substringBeforeLast('.')
                    }
            } ?: throw IllegalStateException("Could not find plugin ID in JAR for ${rootDep.moduleName}")
            return CompilerPluginUseInfo(
                classpath = rootDep.allModuleArtifacts
                    .filter { it.classifier.isNullOrEmpty() && it.extension == "jar" }
                    .map { it.file },
                options = options.entries.map { "plugin:${pluginId}:${it.key}=${it.value}" }
            )
        }
    }
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
            this.pluginClasspaths = compileInfo.compilerPlugins.map { it.path }.plus(pluginInfo.classpath.map { it.path }).toList().toTypedArray()
            this.pluginOptions = compileInfo.pluginOptions.arguments.plus(pluginInfo.options).toTypedArray()
            this.destinationAsFile = compileInfo.buildCache.also { it.mkdirs() }
        }
    )
    if (result.code != 0) {
        throw IllegalStateException("Got a code ${result.code} back from the compiler! ${result.name}")
    }
}

package com.lightningkite.khrysalis.ios.swift

import com.lightningkite.khrysalis.generic.CompilerPluginUseInfo
import com.lightningkite.khrysalis.generic.runCompiler
import com.lightningkite.khrysalis.utils.copyFolderOutFromRes
import org.gradle.api.Project
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
import java.util.*

fun swiftPluginUse(
    project: Project,
    iosBase: File,
    projectName: String = project.name.takeUnless { it == "app" || it == "android" }
        ?: project.rootProject.name,
    libraryMode: Boolean = false
): CompilerPluginUseInfo {
    val dependencies = project.swiftDependencies(iosBase)
    val output = iosBase.resolve(projectName).resolve("src")
    return CompilerPluginUseInfo(
        project = project,
        configName = "khrysalisSwift",
        options = listOfNotNull(
            "plugin:com.lightningkite.khrysalis.swift:outputDirectory=\"${output.path}\"",
            "plugin:com.lightningkite.khrysalis.swift:projName=\"${projectName}\"",
            "plugin:com.lightningkite.khrysalis.swift:equivalents=\"${
                dependencies.joinToString(
                    File.pathSeparator
                )
            }\"",
            "plugin:com.lightningkite.khrysalis.swift:libraryMode=\"${libraryMode}\""
        )
    )
}

fun Project.swiftDependencies(iosBase: File): Sequence<File> {
    val localProperties = Properties().apply {
        val f = project.rootProject.file("local.properties")
        if (f.exists()) {
            load(f.inputStream())
        }
    }
    val pathRegex = Regex(":path => '([^']+)'")
    val home = System.getProperty("user.home")
    val localPodSpecRefs = iosBase
        .resolve("Podfile")
        .takeIf { it.exists() }
        ?.let { file ->
            file
                .readText()
                .let { pathRegex.findAll(it) }
                .map { it.groupValues[1] }
                .map { it.replace("~", home) }
                .map {
                    if (it.startsWith('/'))
                        File(it).parentFile
                    else
                        File(file.parentFile, it).parentFile
                }
        } ?: sequenceOf()
    val allLocations = (localProperties.getProperty("khrysalis.iospods")
        ?: localProperties.getProperty("khrysalis.nonmacmanifest") ?: "")
        .splitToSequence(File.pathSeparatorChar)
        .filter { it.isNotBlank() }
        .map { File(it) }
        .filter { it.exists() }
        .plus(iosBase.resolve("Pods"))
        .plus(localPodSpecRefs)
    return allLocations
}
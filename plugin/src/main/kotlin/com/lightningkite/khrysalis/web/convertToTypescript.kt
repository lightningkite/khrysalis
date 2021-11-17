package com.lightningkite.khrysalis.web

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

fun typescriptPluginUse(project: Project, webBase: File, projectName: String?): CompilerPluginUseInfo {
    return CompilerPluginUseInfo(
        project = project,
        configName = "khrysalisTypescript",
        options = listOfNotNull(
            "plugin:com.lightningkite.khrysalis.typescript:outputDirectory=\"${webBase.resolve("src")}\"",
            projectName?.let { "plugin:com.lightningkite.khrysalis.typescript:projName=\"${it}\"" },
            "plugin:com.lightningkite.khrysalis.typescript:equivalents=\"${webBase}\""
        )
    )
}

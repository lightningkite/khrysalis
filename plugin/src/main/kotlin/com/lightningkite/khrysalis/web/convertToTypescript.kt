package com.lightningkite.khrysalis.web

import com.lightningkite.khrysalis.generic.CompilerPluginUseInfo
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.generic.runCompiler
import com.lightningkite.khrysalis.typescript.KotlinTypescriptCLP
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
        cacheName = "typescript.jar",
        options = listOfNotNull(
            "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY_NAME}=\"${webBase.resolve("src")}\"",
            projectName?.let { "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_PROJECT_NAME_NAME}=\"${it}\"" },
            "plugin:${KotlinTypescriptCLP.PLUGIN_ID}:${KotlinTranspileCLP.KEY_EQUIVALENTS_NAME}=\"${webBase}\""
        )
    )
}

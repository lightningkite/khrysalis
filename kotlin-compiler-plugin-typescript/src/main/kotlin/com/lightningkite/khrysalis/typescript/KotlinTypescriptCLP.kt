package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.SmartTabWriter
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class KotlinTypescriptCLP : CommandLineProcessor {
    companion object {
        const val KEY_ACTUALS_DIRECTORY_NAME = "equivalents"
        val KEY_ACTUALS_DIRECTORIES = CompilerConfigurationKey.create<List<File>>(KEY_ACTUALS_DIRECTORY_NAME)
        const val KEY_OUTPUT_DIRECTORY_NAME = "outputDirectory"
        val KEY_OUTPUT_DIRECTORY = CompilerConfigurationKey.create<File>(KEY_OUTPUT_DIRECTORY_NAME)
        const val PLUGIN_ID = "com.lightningkite.khrysalis.typescript"
    }

    override val pluginId: String get() = PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(KEY_OUTPUT_DIRECTORY_NAME, "A directory", "Where to store the output files.", required = true),
        CliOption(
            KEY_ACTUALS_DIRECTORY_NAME,
            "A directory",
            "Where to look for translational information.",
            required = false
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) =
        when (option.optionName) {
            KEY_ACTUALS_DIRECTORY_NAME -> configuration.put(
                KEY_ACTUALS_DIRECTORIES,
                value.trim('"').split(File.pathSeparatorChar).map { File(it) })
            KEY_OUTPUT_DIRECTORY_NAME -> configuration.put(KEY_OUTPUT_DIRECTORY, value.trim('"').let { File(it) })
            else -> {
            }
        }
}

class KotlinTypescriptCR : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        AnalysisHandlerExtension.registerExtension(
            project,
            KotlinTypescriptExtension(
                configuration[KotlinTypescriptCLP.KEY_ACTUALS_DIRECTORIES] ?: listOf(),
                configuration[KotlinTypescriptCLP.KEY_OUTPUT_DIRECTORY]!!,
                configuration[CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY]
            )
        )
    }
}

class KotlinTypescriptExtension(
    val actuals: List<File>,
    val output: File,
    val collector: MessageCollector?
) : AnalysisHandlerExtension {
    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        collector?.report(CompilerMessageSeverity.INFO, "Completed analysis.")
        val ctx = bindingTrace.bindingContext
        val translator = TypescriptTranslator(ctx, collector)
        try {
            actuals.asSequence()
                .flatMap { it.walkTopDown() }
                .filter {
                    it.name.endsWith(".ts.yaml") || it.name.endsWith(".ts.yml")
                }
                .forEach { actualFile ->
                    collector?.report(CompilerMessageSeverity.INFO, "Reading equivalents from $actualFile...")
                    translator.replacements += actualFile
                }
        } catch (t: Throwable) {
            collector?.report(CompilerMessageSeverity.ERROR, "Failed to parse equivalents:")
            collector?.report(
                CompilerMessageSeverity.ERROR,
                StringWriter().also { t.printStackTrace(PrintWriter(it)) }.buffer.toString()
            )
            return AnalysisResult.compilationError(ctx)
        }
        val commonPath = files.asSequence()
            .map { it.virtualFilePath }
            .takeUnless { it.none() }
            ?.reduce { acc, s -> acc.commonPrefixWith(s) } ?: ""
        for (file in files) {
            if (!file.virtualFilePath.endsWith(".shared.kt")) continue
            try {
                val outputFile = output
                    .resolve(file.virtualFilePath.removePrefix(commonPath))
                    .parentFile
                    .resolve(file.name.removeSuffix(".kt").plus(".ts"))
                collector?.report(CompilerMessageSeverity.INFO, "Translating $file to $outputFile")
                outputFile.parentFile.mkdirs()
                val out = TypescriptFileEmitter(translator)
                translator.translate(file, out)
                outputFile.bufferedWriter().use {
                    out.write(it, file)
                    it.flush()
                }
            } catch (t: Throwable) {
                collector?.report(CompilerMessageSeverity.ERROR, "Failed:")
                collector?.report(
                    CompilerMessageSeverity.ERROR,
                    StringWriter().also { t.printStackTrace(PrintWriter(it)) }.buffer.toString()
                )
            }
        }
        collector?.report(CompilerMessageSeverity.INFO, "Completed translation.")
        return AnalysisResult.Companion.success(ctx, module, false)
    }
}

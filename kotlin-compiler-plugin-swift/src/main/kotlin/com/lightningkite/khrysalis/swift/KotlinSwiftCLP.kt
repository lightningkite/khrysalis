package com.lightningkite.khrysalis.swift

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

class KotlinSwiftCLP : CommandLineProcessor {
    companion object {
        const val KEY_DEPENDENCIES_NAME = "swiftDependencies"
        val KEY_SWIFT_DEPENDENCIES = CompilerConfigurationKey.create<List<File>>(KEY_DEPENDENCIES_NAME)
        const val KEY_PROJECT_NAME_NAME = "projName"
        val KEY_PROJECT_NAME = CompilerConfigurationKey.create<String>(KEY_PROJECT_NAME_NAME)
        const val KEY_OUTPUT_DIRECTORY_NAME = "outputDirectory"
        val KEY_OUTPUT_DIRECTORY = CompilerConfigurationKey.create<File>(KEY_OUTPUT_DIRECTORY_NAME)
        const val PLUGIN_ID = "com.lightningkite.khrysalis.swift"
    }

    override val pluginId: String get() = PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            KEY_OUTPUT_DIRECTORY_NAME,
            "A directory",
            "Where to store the output files.",
            required = true
        ),
        CliOption(
            KEY_DEPENDENCIES_NAME,
            "A list of directories",
            "Where to look for translational information.",
            required = false
        ),
        CliOption(
            KEY_PROJECT_NAME_NAME,
            "Name of the NPM project",
            "Name of the NPM project, specifically for handling equivalent imports correctly.",
            required = false
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) =
        when (option.optionName) {
            KEY_DEPENDENCIES_NAME -> configuration.put(
                KEY_SWIFT_DEPENDENCIES,
                value.trim('"').split(File.pathSeparatorChar).map { File(it) })
            KEY_OUTPUT_DIRECTORY_NAME -> configuration.put(KEY_OUTPUT_DIRECTORY, value.trim('"').let { File(it) })
            KEY_PROJECT_NAME_NAME -> configuration.put(KEY_PROJECT_NAME, value.trim('"'))
            else -> {
            }
        }
}

class KotlinSwiftCR : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        AnalysisHandlerExtension.registerExtension(
            project,
            KotlinSwiftExtension(
                configuration[KotlinSwiftCLP.KEY_PROJECT_NAME],
                configuration[KotlinSwiftCLP.KEY_SWIFT_DEPENDENCIES] ?: listOf(),
                configuration[KotlinSwiftCLP.KEY_OUTPUT_DIRECTORY]!!,
                configuration[CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY]
            )
        )
    }
}

class KotlinSwiftExtension(
    val projectName: String? = null,
    val dependencies: List<File>,
    val output: File,
    val collector: MessageCollector?
) : AnalysisHandlerExtension {
    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {

        collector?.report(CompilerMessageSeverity.INFO, "Completed analysis for ${projectName}.")
        val ctx = bindingTrace.bindingContext
        val translator = SwiftTranslator(
            projectName = projectName,
            bindingContext = ctx,
            commonPath = files.asSequence()
                .map { it.virtualFilePath }
                .filter { it.endsWith(".shared.kt") }
                .takeUnless { it.none() }
                ?.reduce { acc, s -> acc.commonPrefixWith(s) }
                ?.substringBeforeLast('/')
                ?.plus('/')
                ?.also { println("Common file path: $it") } ?: "",
            collector = collector
        )
        println("Output: $output")

        //Load manifests
        dependencies.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.name == "fqnames.txt" }
            .forEach {
                val lines = it.readLines().filter { it.isNotBlank() }
                val name = lines.first()
                lines.drop(1).forEach {
                    translator.fqToImport[it] = name
                }
            }

        //Load equivalents
        dependencies.asSequence().plus(output)
            .flatMap { it.walkTopDown() }
            .filter {
                it.name.endsWith(".swift.yaml") || it.name.endsWith(".swift.yml")
            }
            .forEach { actualFile ->
                try {
                    collector?.report(CompilerMessageSeverity.INFO, "Reading equivalents from $actualFile...")
                    translator.replacements += actualFile
                } catch (t: Throwable) {
                    collector?.report(CompilerMessageSeverity.ERROR, "Failed to parse equivalents for $actualFile:")
                    collector?.report(
                        CompilerMessageSeverity.ERROR,
                        StringWriter().also { t.printStackTrace(PrintWriter(it)) }.buffer.toString()
                    )
                    return AnalysisResult.compilationError(ctx)
                }
            }


        for (file in files) {
            if (!file.virtualFilePath.endsWith(".shared.kt")) {
                collector?.report(CompilerMessageSeverity.INFO, "Skipping $file")
                continue
            }
            try {
                val outputFile = output
                    .resolve(file.virtualFilePath.removePrefix(translator.commonPath))
                    .parentFile
                    .resolve(file.name.removeSuffix(".kt").plus(".swift"))
                if (outputFile.exists() && outputFile.useLines { it.first() } != SwiftFileEmitter.overwriteWarning) continue
                collector?.report(CompilerMessageSeverity.INFO, "Translating $file to $outputFile")
                outputFile.parentFile.mkdirs()
                val out = SwiftFileEmitter(translator, file)
                translator.translate(file, out)
                outputFile.bufferedWriter().use {
                    out.write(it, file)
                    it.flush()
                }
            } catch (t: Throwable) {
                collector?.report(CompilerMessageSeverity.WARNING, "Failed to convert $file:")
                collector?.report(
                    CompilerMessageSeverity.ERROR,
                    StringWriter().also { t.printStackTrace(PrintWriter(it)) }.buffer.toString()
                )
            }
        }
        collector?.report(CompilerMessageSeverity.INFO, "Writing manifest file...")
        output.resolve("fqnames.txt").bufferedWriter().use {
            sequenceOf(projectName ?: "Module").plus(
                files.asSequence()
                    .flatMap { f ->
                        f.declarations.asSequence()
                            .mapNotNull { it as? KtNamedDeclaration }
                            .mapNotNull { it.fqName?.asString() }
                            .plus(f.packageFqName.asString())
                    }
            )
                .distinct()
                .forEach { line ->
                    it.appendln(line)
                }
        }
        collector?.report(CompilerMessageSeverity.INFO, "Completed translation.")
        return AnalysisResult.Companion.success(ctx, module, false)
    }
}

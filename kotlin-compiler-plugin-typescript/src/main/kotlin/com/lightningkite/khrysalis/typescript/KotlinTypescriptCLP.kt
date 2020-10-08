package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.determineTranslatable
import com.lightningkite.khrysalis.typescript.manifest.generateFqToFileMap
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
import org.jetbrains.kotlin.psi.psiUtil.toVisibility
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class KotlinTypescriptCLP : CommandLineProcessor {
    companion object {
        const val KEY_TS_DEPENDENCIES_NAME = "tsDependencies"
        val KEY_TS_DEPENDENCIES = CompilerConfigurationKey.create<List<File>>(KEY_TS_DEPENDENCIES_NAME)
        const val KEY_PROJECT_NAME_NAME = "projName"
        val KEY_PROJECT_NAME = CompilerConfigurationKey.create<String>(KEY_PROJECT_NAME_NAME)
        const val KEY_OUTPUT_DIRECTORY_NAME = "outputDirectory"
        val KEY_OUTPUT_DIRECTORY = CompilerConfigurationKey.create<File>(KEY_OUTPUT_DIRECTORY_NAME)
        const val PLUGIN_ID = "com.lightningkite.butterfly.typescript"
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
            KEY_TS_DEPENDENCIES_NAME,
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
            KEY_TS_DEPENDENCIES_NAME -> configuration.put(
                KEY_TS_DEPENDENCIES,
                value.trim('"').split(File.pathSeparatorChar).map { File(it) })
            KEY_OUTPUT_DIRECTORY_NAME -> configuration.put(KEY_OUTPUT_DIRECTORY, value.trim('"').let { File(it) })
            KEY_PROJECT_NAME_NAME -> configuration.put(KEY_PROJECT_NAME, value.trim('"'))
            else -> {
            }
        }
}

class KotlinTypescriptCR : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        AnalysisHandlerExtension.registerExtension(
            project,
            KotlinTypescriptExtension(
                configuration[KotlinTypescriptCLP.KEY_PROJECT_NAME],
                configuration[KotlinTypescriptCLP.KEY_TS_DEPENDENCIES] ?: listOf(),
                configuration[KotlinTypescriptCLP.KEY_OUTPUT_DIRECTORY]!!,
                configuration[CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY]
            )
        )
    }
}

class KotlinTypescriptExtension(
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
        val translator = TypescriptTranslator(
            projectName = projectName,
            bindingContext = ctx,
            commonPath = files.asSequence()
                .filter { determineTranslatable(it) }
                .map { it.virtualFilePath }
                .takeUnless { it.none() }
                ?.reduce { acc, s -> acc.commonPrefixWith(s) }
                ?.substringBeforeLast('/')
                ?.plus('/')
                ?.also { println("Common file path: $it") } ?: "",
            collector = collector
        )
        println("Output: $output")

        //Load other declarations
        translator.declarations.load(dependencies.asSequence().plus(output), output)

        //Create manifest of declarations within this module
        val map: Map<String, File> =
            translator.run { generateFqToFileMap(files.filter { determineTranslatable(it) }, output) }
        translator.declarations.local.putAll(map)

        //Load equivalents
        dependencies.asSequence().plus(output)
            .flatMap { it.walkTopDown() }
            .filter {
                it.name.endsWith(".ts.yaml") || it.name.endsWith(".ts.yml")
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
            if(!determineTranslatable(file)) continue
            try {
                val outputFile = output
                    .resolve(file.virtualFilePath.removePrefix(translator.commonPath))
                    .parentFile
                    .resolve(file.name.removeSuffix(".kt").plus(".ts"))
                if (outputFile.exists() && outputFile.useLines { it.first() } != TypescriptFileEmitter.overwriteWarning) continue
                collector?.report(CompilerMessageSeverity.INFO, "Translating $file to $outputFile")
                outputFile.parentFile.mkdirs()
                val out = TypescriptFileEmitter(translator, file)
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

        output.resolve("../build/manifest.txt").also { it.parentFile.mkdirs() }.bufferedWriter().use {
            for(entry in translator.declarations.local){
                it.appendln("${entry.key} = ${entry.value}")
            }
            for(entry in translator.declarations.node){
                it.appendln("${entry.key} = ${entry.value}")
            }
        }

        collector?.report(CompilerMessageSeverity.INFO, "Completed translation.")
        return AnalysisResult.Companion.success(ctx, module, false)
    }
}

package com.lightningkite.khrysalis.generic

import com.fasterxml.jackson.databind.ObjectMapper
import com.lightningkite.khrysalis.analysis.bindingContext
import com.lightningkite.khrysalis.analysis.releaseBindingContext
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.replacements.replacements
import com.lightningkite.khrysalis.shouldBeTranslated
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

abstract class KotlinTranspileCLP : CommandLineProcessor {
    companion object {
        const val KEY_EQUIVALENTS_NAME = "equivalents"
        val KEY_EQUIVALENTS = CompilerConfigurationKey.create<List<File>>(KEY_EQUIVALENTS_NAME)
        const val KEY_PROJECT_NAME_NAME = "projName"
        val KEY_PROJECT_NAME = CompilerConfigurationKey.create<String>(KEY_PROJECT_NAME_NAME)
        const val KEY_OUTPUT_DIRECTORY_NAME = "outputDirectory"
        val KEY_OUTPUT_DIRECTORY = CompilerConfigurationKey.create<File>(KEY_OUTPUT_DIRECTORY_NAME)
        const val KEY_COMMON_PACKAGE_NAME = "commonPackage"
        val KEY_COMMON_PACKAGE = CompilerConfigurationKey.create<String>(KEY_COMMON_PACKAGE_NAME)
        const val KEY_LIBRARY_MODE_NAME = "libraryMode"
        val KEY_LIBRARY_MODE = CompilerConfigurationKey.create<Boolean>(KEY_LIBRARY_MODE_NAME)
    }

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            KEY_COMMON_PACKAGE_NAME,
            "A package name",
            "The package name to automatically remove as a prefix",
            required = false
        ),
        CliOption(
            KEY_OUTPUT_DIRECTORY_NAME,
            "A directory",
            "Where to store the output files.",
            required = true
        ),
        CliOption(
            KEY_EQUIVALENTS_NAME,
            "A list of directories",
            "Where to look for translational information.",
            required = false
        ),
        CliOption(
            KEY_PROJECT_NAME_NAME,
            "Name of the iOS module name",
            "Name of the iOS module name, specifically for imports.",
            required = true
        ),
        CliOption(
            KEY_LIBRARY_MODE_NAME,
            "Library mode",
            "Should additional files for use as a library be generated?",
            required = false
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) =
        when (option.optionName) {
            KEY_EQUIVALENTS_NAME -> configuration.put(
                KEY_EQUIVALENTS,
                value.trim('"').split(File.pathSeparatorChar).map { File(it) })
            KEY_COMMON_PACKAGE_NAME -> configuration.put(KEY_COMMON_PACKAGE, value.trim('"'))
            KEY_OUTPUT_DIRECTORY_NAME -> configuration.put(KEY_OUTPUT_DIRECTORY, File(value.trim('"')))
            KEY_PROJECT_NAME_NAME -> configuration.put(KEY_PROJECT_NAME, value.trim('"'))
            KEY_LIBRARY_MODE_NAME -> configuration.put(KEY_LIBRARY_MODE, value.toBoolean())
            else -> {
            }
        }
}

abstract class KotlinTranspileCR : ComponentRegistrar {
    abstract val replacementMapper: ObjectMapper
    abstract val fileExtension: String
    abstract fun makeExtension(
        projectName: String,
        dependencies: List<File>,
        equivalents: Replacements,
        commonPackage: String?,
        outputDirectory: File,
        libraryMode: Boolean,
        collector: MessageCollector
    ): AnalysisHandlerExtension

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val libraryMode = configuration[KotlinTranspileCLP.KEY_LIBRARY_MODE]!!
        val projectName = configuration[KotlinTranspileCLP.KEY_PROJECT_NAME]!!
        val dependencies = configuration[KotlinTranspileCLP.KEY_EQUIVALENTS] ?: listOf()
        val commonPackage = configuration[KotlinTranspileCLP.KEY_COMMON_PACKAGE]
        val output = configuration[KotlinTranspileCLP.KEY_OUTPUT_DIRECTORY]!!
        val collector = configuration[CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY]

        val reps = Replacements(replacementMapper)
        replacements = reps
        dependencies.asSequence().plus(output)
            .also { collector?.report(CompilerMessageSeverity.INFO, "Looking for equivalents in ${it.joinToString()}") }
            .flatMap { it.walkTopDown() }
            .filter {
                it.name.endsWith(".$fileExtension.yaml") || it.name.endsWith(".$fileExtension.yml")
            }
            .forEach { actualFile ->
                try {
                    reps += actualFile
                    if(actualFile.readText().contains("LKButterfly")) {
                        throw IllegalArgumentException("Old translation file found at ${actualFile}")
                    }
                } catch (t: Throwable) {
                    collector?.report(CompilerMessageSeverity.ERROR, "Failed to parse equivalents for $actualFile:")
                    collector?.report(
                        CompilerMessageSeverity.ERROR,
                        StringWriter().also { t.printStackTrace(PrintWriter(it)) }.buffer.toString()
                    )
                    throw t
                }
            }

        AnalysisHandlerExtension.registerExtension(
            project,
            makeExtension(
                projectName,
                dependencies,
                reps,
                commonPackage,
                output,
                libraryMode,
                collector ?: object : MessageCollector {
                    override fun clear() {}
                    var errors = false
                    override fun hasErrors(): Boolean = errors
                    override fun report(
                        severity: CompilerMessageSeverity,
                        message: String,
                        location: CompilerMessageSourceLocation?
                    ) {
                        if(location != null)
                            println("$location: $message")
                        else
                            println(message)
                    }
                },
            )
        )
    }
}

fun KtFile.outputRelativePath(packagePrefix: String?, outputExtension: String): String = packageFqName.asString()
    .replace('.', '/')
    .removePrefix((packagePrefix ?: "").replace('.', '/'))
    .trim('/')
    .let { if(it.isBlank()) it else "$it/" }
    .plus(name.removeSuffix(".kt").plus(".$outputExtension"))

abstract class KotlinTranspileExtension(
    val projectName: String,
    val commonPackage: String?,
    val outputDirectory: File,
    val libraryMode: Boolean,
    val collector: MessageCollector
) : AnalysisHandlerExtension {
    abstract val outputExtension: String

    open fun start(
        context: BindingContext,
        files: Collection<KtFile>
    ){}
    abstract fun transpile(
        context: BindingContext,
        file: KtFile
    ): CharSequence
    open fun finish(
        context: BindingContext,
        files: Collection<KtFile>
    ){}

    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult {
        bindingContext = bindingTrace.bindingContext
        collector.report(CompilerMessageSeverity.INFO, "Completed analysis for ${projectName}.")
        outputDirectory.mkdirs()

        val filesToTranslate = files.filter { it.shouldBeTranslated() }
        collector.report(CompilerMessageSeverity.LOGGING, "filesToTranslate ${filesToTranslate}.")

        start(bindingTrace.bindingContext, files)

        val outputFiles = HashSet<File>()
        for(file in filesToTranslate){
            try {
                val outputFile = outputDirectory.resolve(file.outputRelativePath(commonPackage, outputExtension))
                outputFile.parentFile.mkdirs()
                val existing = outputFile.takeIf { it.exists() }?.readText()
                if(existing != null && !FileEmitter.canBeOverwritten(existing)) {
                    collector.report(CompilerMessageSeverity.LOGGING, "Skipping ${file.virtualFilePath}.")
                    continue
                }
                collector.report(CompilerMessageSeverity.LOGGING, "Translating ${file.virtualFilePath} to ${outputFile}.")
                val output = transpile(bindingTrace.bindingContext, file)
                outputFiles.add(outputFile)
                if (existing != output)
                    outputFile.writeText(output.toString())
            } catch(e: Exception){
                collector.report(CompilerMessageSeverity.ERROR, "Got error: ${e.stackTraceToString()}.")
            }
        }

        finish(bindingTrace.bindingContext, files)

        collector.report(CompilerMessageSeverity.INFO, "Completed translation.")
        releaseBindingContext()
        return AnalysisResult.Companion.success(bindingTrace.bindingContext, module, true)
    }
}

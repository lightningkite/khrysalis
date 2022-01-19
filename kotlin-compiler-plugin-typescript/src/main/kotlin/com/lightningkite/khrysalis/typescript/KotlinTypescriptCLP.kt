package com.lightningkite.khrysalis.typescript

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.generic.KotlinTranspileCR
import com.lightningkite.khrysalis.generic.KotlinTranspileExtension
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.shouldBeTranslated
import com.lightningkite.khrysalis.typescript.manifest.generateFqToFileMap
import com.lightningkite.khrysalis.typescript.replacements.TypescriptJacksonReplacementsModule
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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class KotlinTypescriptCLP : KotlinTranspileCLP() {
    companion object {
        const val PLUGIN_ID = "com.lightningkite.khrysalis.typescript"
    }
    override val pluginId: String
        get() = PLUGIN_ID
}

class KotlinTypescriptCR : KotlinTranspileCR() {

    companion object {
        val replacementMapper = ObjectMapper(YAMLFactory())
            .registerModule(TypescriptJacksonReplacementsModule())
            .registerKotlinModule()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
    override val replacementMapper: ObjectMapper
        get() = Companion.replacementMapper
    override val fileExtension: String
        get() = "ts"

    override fun makeExtension(
        projectName: String,
        dependencies: List<File>,
        equivalents: Replacements,
        input: File?,
        outputDirectory: File,
        libraryMode: Boolean,
        collector: MessageCollector
    ): AnalysisHandlerExtension = KotlinTypescriptExtension(
        projectName,
        dependencies,
        equivalents,
        input,
        outputDirectory,
        libraryMode,
        collector
    )
}

class KotlinTypescriptExtension(
    projectName: String,
    val dependencies: List<File>,
    val replacements: Replacements,
    input: File?,
    outputDirectory: File,
    libraryMode: Boolean,
    collector: MessageCollector
) : KotlinTranspileExtension(
    projectName,
    input,
    outputDirectory,
    libraryMode,
    collector
) {
    override val outputExtension: String
        get() = "ts"

    lateinit var translator: TypescriptTranslator

    override fun start(context: BindingContext, files: Collection<KtFile>) {
        println("Will generate files ${outputDirectory.resolve("fqnames.txt")}, ${outputDirectory.resolve("index.ts")}...")
        translator = TypescriptTranslator(projectName, commonPath, collector, replacements)

        // Load node declarations
        translator.declarations.loadNonlocal(dependencies, filterOut = outputDirectory)

        // Load local declarations
        translator.declarations.load(outputDirectory)

        // Create manifest of declarations within this module
        translator.declarations.local.putAll(translator.run { generateFqToFileMap(files.filter { it.shouldBeTranslated() }) })
    }

    override fun transpile(context: BindingContext, file: KtFile): CharSequence {
        val out = TypescriptFileEmitter(translator, file)
        translator.translate(file, out)
        val str = StringWriter()
        str.buffered().use {
            out.write(it, file)
            it.flush()
        }
        return str.buffer
    }

    override fun finish(context: BindingContext, files: Collection<KtFile>) {
        if(libraryMode) {
            outputDirectory.resolve("fqnames.txt").bufferedWriter().use {
                it.appendLine(translator.projectName)
                for(entry in translator.declarations.local){
                    it.appendLine(entry.key)
                }
            }
            outputDirectory.resolve("index.ts").bufferedWriter().use {
                translator.declarations.local.values.distinct().forEach { f ->
                    it.appendLine("export * from './${f.removeSuffix(".ts")}'")
                }
            }
        }
    }
}

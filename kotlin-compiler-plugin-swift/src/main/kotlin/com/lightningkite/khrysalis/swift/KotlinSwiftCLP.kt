package com.lightningkite.khrysalis.swift

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.generic.KotlinTranspileCR
import com.lightningkite.khrysalis.generic.KotlinTranspileExtension
import com.lightningkite.khrysalis.generic.TranspileConfig
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.swift.replacements.SwiftJacksonReplacementsModule
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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class KotlinSwiftCLP : KotlinTranspileCLP() {
    companion object {
        const val PLUGIN_ID = "com.lightningkite.khrysalis.swift"
    }
    override val pluginId: String
        get() = PLUGIN_ID

    init {
        println("My plugin ID is $pluginId")
    }
}

class KotlinSwiftCR : KotlinTranspileCR() {
    companion object {
        val replacementMapper = ObjectMapper(YAMLFactory())
        .registerModule(SwiftJacksonReplacementsModule())
        .registerKotlinModule()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
    override val replacementMapper: ObjectMapper
        get() = Companion.replacementMapper
    override val fileExtension: String
        get() = "swift"

    override fun makeExtension(config: TranspileConfig, collector: MessageCollector): AnalysisHandlerExtension = KotlinSwiftExtension(
        config,
        collector
    )
}

class KotlinSwiftExtension(
    config: TranspileConfig,
    collector: MessageCollector
) : KotlinTranspileExtension(
    config,
    collector
) {
    override val outputExtension: String
        get() = "swift"

    lateinit var translator: SwiftTranslator

    override fun start(context: BindingContext, files: Collection<KtFile>) {
        translator = SwiftTranslator(config.projName, collector, config.replacements)
    }

    override fun transpile(context: BindingContext, file: KtFile): CharSequence {
        val out = SwiftFileEmitter(translator, file)
        translator.translate(file, out)
        val str = StringWriter()
        str.buffered().use {
            out.write(it, file)
            it.flush()
        }
        return str.buffer
    }

    override fun finish(context: BindingContext, files: Collection<KtFile>) {
        // Write manifest (AKA list of FQ names in module)
        if(config.libraryMode) {
            config.outputFqnames.bufferedWriter().use {
                sequenceOf(config.projName).plus(
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
        }
    }
}

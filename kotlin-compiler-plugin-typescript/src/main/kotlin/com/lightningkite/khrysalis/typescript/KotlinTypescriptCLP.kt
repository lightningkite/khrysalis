package com.lightningkite.khrysalis.typescript

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lightningkite.khrysalis.generic.KotlinTranspileCLP
import com.lightningkite.khrysalis.generic.KotlinTranspileCR
import com.lightningkite.khrysalis.generic.KotlinTranspileExtension
import com.lightningkite.khrysalis.generic.TranspileConfig
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.shouldBeTranslated
import com.lightningkite.khrysalis.typescript.manifest.generateFqToFileMap
import com.lightningkite.khrysalis.typescript.replacements.TypescriptJacksonReplacementsModule
import com.lightningkite.khrysalis.util.unixPath
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File
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

    override fun makeExtension(config: TranspileConfig, collector: MessageCollector): AnalysisHandlerExtension = KotlinTypescriptExtension(
        config,
        collector
    )
}

class KotlinTypescriptExtension(
    config: TranspileConfig,
    collector: MessageCollector
) : KotlinTranspileExtension(
    config,
    collector
) {
    override val outputExtension: String
        get() = "ts"

    lateinit var translator: TypescriptTranslator

    override fun start(context: BindingContext, files: Collection<KtFile>) {
        translator = TypescriptTranslator(config.projName, config.commonPackage, config.outputDirectory, collector, config.replacements)

        val outputSrc = when {
            config.outputDirectory.resolve("node_modules").exists() -> config.outputDirectory
            config.outputDirectory.resolve("../node_modules").exists() -> config.outputDirectory
            config.outputDirectory.resolve("../../node_modules").exists() -> config.outputDirectory.parentFile
            else -> config.outputDirectory
        }

        // Load node declarations
        translator.declarations.node += config.replacements.direct

        // Load local declarations
        println("Loading locals from $outputSrc")
        translator.declarations.loadLocal(outputSrc)

        // Create manifest of declarations within this module
        translator.declarations.local.putAll(translator.run { generateFqToFileMap(outputDirectory, files.filter { it.shouldBeTranslated() }) })
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
        if(config.libraryMode) {
            config.outputFqnames.bufferedWriter().use {
                it.appendLine(translator.projectName)
                for(entry in translator.declarations.local){
                    it.appendLine(entry.key)
                }
            }
            config.outputDirectory.resolve("index.ts").bufferedWriter().use {
                config.outputDirectory.walkTopDown().filter { it.extension == "ts" }.forEach { f ->
                    it.appendLine("export * from './${f.relativeTo(config.outputDirectory).unixPath.removeSuffix(".ts")}'")
                }
            }
        }
    }
}

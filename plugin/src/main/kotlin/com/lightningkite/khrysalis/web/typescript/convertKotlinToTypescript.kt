package com.lightningkite.khrysalis.web.typescript

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.VERSION
import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.preparse.preparseKotlinFiles
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.ios.swift.FileConversionInfo
import com.lightningkite.khrysalis.preparse.plus
import com.lightningkite.khrysalis.web.typescript.actuals.typescriptStubs
import com.lightningkite.khrysalis.utils.Versioned
import com.lightningkite.khrysalis.utils.checksum
import com.lightningkite.khrysalis.utils.forEachMultithreaded
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun convertKotlinToTypescript(
    androidMainFolder: File,
    webFolder: File,
    clean: Boolean = false,
    typescript: TypescriptTranslator
) = convertKotlinToTypescriptByFolder(
    interfacesOut = androidMainFolder.resolve("resources/META-INF/khrysalis-interfaces.json"),
    baseKotlin = androidMainFolder.resolve("java"),
    baseTs = webFolder,
    clean = clean,
    typescript = typescript
)

fun convertKotlinToTypescriptByFolder(
    interfacesOut: File,
    baseKotlin: File,
    baseTs: File,
    clean: Boolean = false,
    typescript: TypescriptTranslator
) {

    val toConvert = baseKotlin.walkTopDown()
        .filter { it.extension == "kt" }
        .filter { it.name.contains(".shared") }

    val preparseData = preparseKotlinFiles(
        toConvert,
        interfacesOut,
        baseKotlin
    )
    typescript.preparseData += preparseData

    val cacheFile = baseKotlin.resolve("khrysalis-ts-cache-2.json")
    val existingCache = if (cacheFile.exists()) {
        val versioned = jacksonObjectMapper().readValue<Versioned<Map<String, FileConversionInfo>>>(cacheFile)
        if (versioned.version == VERSION) versioned.value else mapOf()
    } else
        mapOf<String, FileConversionInfo>()
    val newCache = HashMap<String, FileConversionInfo>()

    toConvert.forEachMultithreaded { file ->
        val output = File(
            baseTs.resolve(file.relativeTo(baseKotlin))
                .toString()
                .removeSuffix("kt")
                .plus("ts")
        )
        val relativeInput = file.relativeTo(baseKotlin).path
        val relativeOutput = output.relativeTo(baseTs).path
        output.parentFile.mkdirs()

        val inputHash = file.checksum()
        val outputHash = if (output.exists()) output.checksum() else 0L
        val existing = existingCache[relativeInput]
        val cache =
            if (!clean && existing != null && existing.inputHash == inputHash && existing.outputHash == outputHash) {
                existing
            } else {
                log(
                    "Converting ${relativeInput}"
                )

                file.inputStream().buffered().use { stream ->
                    val lexer = KotlinLexer(ANTLRInputStream(stream))
                    val tokenStream = CommonTokenStream(lexer)
                    val parser = KotlinParser(tokenStream)

                    var errorOccurred = false
                    parser.addErrorListener(object : ANTLRErrorListener {
                        override fun reportAttemptingFullContext(
                            p0: Parser?,
                            p1: DFA?,
                            p2: Int,
                            p3: Int,
                            p4: BitSet?,
                            p5: ATNConfigSet?
                        ) {
                        }

                        override fun syntaxError(
                            p0: Recognizer<*, *>?,
                            p1: Any?,
                            p2: Int,
                            p3: Int,
                            p4: String?,
                            p5: RecognitionException?
                        ) {
                            errorOccurred = true
                        }

                        override fun reportAmbiguity(
                            p0: Parser?,
                            p1: DFA?,
                            p2: Int,
                            p3: Int,
                            p4: Boolean,
                            p5: BitSet?,
                            p6: ATNConfigSet?
                        ) {
                        }

                        override fun reportContextSensitivity(
                            p0: Parser?,
                            p1: DFA?,
                            p2: Int,
                            p3: Int,
                            p4: Int,
                            p5: ATNConfigSet?
                        ) {
                        }

                    })

                    try {
                        val outputText = buildString {
                            val tabs = SmartTabWriter(this)
                            typescript.translate(parser.kotlinFile(), tabs)
                        }
                        output.writeText(outputText)

                        FileConversionInfo(
                            path = relativeInput,
                            inputHash = if (errorOccurred) "" else inputHash,
                            outputHash = output.checksum(),
                            outputPath = relativeOutput
                        )
                    } catch (e: Exception) {
                        System.err.println("Failed to convert file $file")
                        e.printStackTrace(System.err)
                        null
                    }
                }
            }
        if (cache != null) {
            newCache[cache.path] = cache
        }
    }

    //Handle actuals
    baseKotlin.walkTopDown()
        .filter { it.extension == "kt" }
        .filter { it.name.contains(".actual") }
        .forEachMultithreaded { file ->
            val output = File(
                baseTs.resolve(file.relativeTo(baseKotlin))
                    .toString()
                    .removeSuffix("kt")
                    .plus("ts")
            )
            val relativeInput = file.relativeTo(baseKotlin).path
            val relativeOutput = output.relativeTo(baseTs).path
            output.parentFile.mkdirs()

            val inputHash = file.checksum()
            val outputHash = if (output.exists()) output.checksum() else 0
            val existing = existingCache[relativeInput]
            val cache =
                if (!clean && existing != null && existing.inputHash == inputHash && existing.outputHash == outputHash) {
                    existing
                } else {
                    log(
                        "Stubbing ${file.absolutePath.substringBeforeLast('.').commonSuffixWith(
                            output.absolutePath.substringBeforeLast(
                                '.'
                            )
                        )}"
                    )

                    try {
                        file.typescriptStubs(typescript, output)

                        FileConversionInfo(
                            path = relativeInput,
                            inputHash = inputHash,
                            outputHash = output.checksum(),
                            outputPath = relativeOutput
                        )
                    } catch (e: Exception) {
                        System.err.println("Failed to convert file $file")
                        e.printStackTrace(System.err)
                        null
                    }
                }
            if (cache != null) {
                newCache[cache.path] = cache
            }
        }

    if (!cacheFile.exists()) {
        cacheFile.parentFile.mkdirs()
        cacheFile.createNewFile()
    }
    cacheFile.writeText(jacksonObjectMapper().writeValueAsString(Versioned(VERSION, newCache)))

    //Clean up old files
    val writtenFiles = newCache.values.asSequence().map { it.outputPath }.toSet()
    baseTs.walkTopDown()
        .filter { it.extension == "ts" }
        .filter { it.name.contains(".shared") }
        .filter { it.relativeTo(baseTs).path !in writtenFiles }
        .forEach { it.delete() }
}

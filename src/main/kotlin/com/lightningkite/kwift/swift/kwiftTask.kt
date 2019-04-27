package com.lightningkite.kwift.swift

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.kwift.VERSION
import com.lightningkite.kwift.interfaces.getInterfaces
import com.lightningkite.kwift.utils.Versioned
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File
import java.util.*
import kotlin.collections.HashMap


fun kwiftTask(pairs: List<Pair<File, File>>) {
    val interfaces = getInterfaces(pairs)
    println("Interfaces: ${interfaces.values.joinToString("\n")}")

    val cacheFile = File("./build/kwift-cache.json")
    val existingCache = if (cacheFile.exists()) {
        val versioned = jacksonObjectMapper().readValue<Versioned<Map<String, FileConversionInfo>>>(cacheFile)
        if (versioned.version == VERSION) versioned.value else mapOf()
    } else
        mapOf<String, FileConversionInfo>()
    val newCache = HashMap<String, FileConversionInfo>()

    pairs.forEach { (directory, outputDirectory) ->
        directory.walkTopDown()
            .filter { it.extension == "kt" }
            .forEach { file ->
                val output = File(
                    outputDirectory.resolve(file.relativeTo(directory))
                        .toString()
                        .removeSuffix("kt")
                        .plus("swift")
                )
                output.parentFile.mkdirs()

                println("File: $file")

                val text = file.readText()
                val inputHash = text.hashCode()
                val outputHash = if(output.exists()) output.readText().hashCode() else 0
                val existing = existingCache[file.path]
                val cache = if(existing != null && existing.inputHash == inputHash && existing.outputHash == outputHash){
                    existing
                } else {
                    val lexer = KotlinLexer(ANTLRInputStream(text.ignoreKotlinOnly()))
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
                        ) {}

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
                        ) {}

                        override fun reportContextSensitivity(
                            p0: Parser?,
                            p1: DFA?,
                            p2: Int,
                            p3: Int,
                            p4: Int,
                            p5: ATNConfigSet?
                        ) {}

                    })

                    val listener = SwiftListener(tokenStream, parser, interfaces)
                    ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())
                    val outputText = "import Foundation\n" + listener.layers.last().last().toOutputString().retabSwift()

                    output.writeText(outputText)

                    FileConversionInfo(
                        path = file.path,
                        inputHash = if(errorOccurred) 0 else inputHash,
                        outputHash = outputText.hashCode(),
                        outputPath = output.path
                    )
                }
                newCache[cache.path] = cache
            }
    }
    if(!cacheFile.exists()){
        cacheFile.parentFile.mkdirs()
        cacheFile.createNewFile()
    }
    cacheFile.writeText(jacksonObjectMapper().writeValueAsString(Versioned(VERSION, newCache)))

    //Clean up old files
    val writtenFiles = newCache.values.asSequence().map { it.outputPath }.toSet()
    pairs.asSequence()
        .map { it.second }
        .flatMap { it.walkTopDown() }
        .filter { it.path !in writtenFiles }
        .forEach { it.delete() }
}

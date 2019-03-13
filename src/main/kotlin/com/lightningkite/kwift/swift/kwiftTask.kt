package com.lightningkite.kwift.swift

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.kwift.VERSION
import com.lightningkite.kwift.interfaces.getInterfaces
import com.lightningkite.kwift.utils.Versioned
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File


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

                    val listener = SwiftListener(tokenStream, parser, interfaces)
                    ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())
                    val outputText = "import Foundation\n" + listener.layers.last().last().toOutputString().retabSwift()

                    output.writeText(outputText)

                    FileConversionInfo(
                        path = file.path,
                        inputHash = inputHash,
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

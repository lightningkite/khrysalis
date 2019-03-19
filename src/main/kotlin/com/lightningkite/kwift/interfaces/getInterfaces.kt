package com.lightningkite.kwift.interfaces

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.kwift.INTERFACE_SCAN_VERSION
import com.lightningkite.kwift.VERSION
import com.lightningkite.kwift.swift.ignoreKotlinOnly
import com.lightningkite.kwift.utils.Versioned
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File

fun getInterfaces(pairs: List<Pair<File, File>>): Map<String, InterfaceListener.InterfaceData> {
    val interfaces = HashMap<String, InterfaceListener.InterfaceData>()

    val cacheFile = File("./build/kwift-interfaces-cache.json")
    val existingCache: Map<String, FileCache> = if (cacheFile.exists()) {
        val versioned = jacksonObjectMapper().readValue<Versioned<Map<String, FileCache>>>(cacheFile)
        if (versioned.version == INTERFACE_SCAN_VERSION) versioned.value else mapOf()
    } else
        mapOf()
    val newCache = HashMap<String, FileCache>()

    pairs.forEach {
        it.first.walkTopDown()
            .filter { it.extension == "kt" }
            .forEach { file ->
                println("File: $file")
                val text = file.readText()
                val hash = text.hashCode()
                val existing = existingCache[file.path]
                val cache = if (existing != null && existing.hash == hash) {
                    existing
                } else {
                    val lexer = KotlinLexer(ANTLRInputStream(text.ignoreKotlinOnly()))
                    val tokenStream = CommonTokenStream(lexer)
                    val parser = KotlinParser(tokenStream)

                    val listener = InterfaceListener(parser)
                    ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())
                    FileCache(
                        path = file.path,
                        hash = hash,
                        data = listener.interfaces
                    )
                }
                newCache[cache.path] = cache

                for (i in cache.data) {
                    interfaces[i.qualifiedName] = i
                }
            }
    }

    if (!cacheFile.exists()) {
        cacheFile.parentFile.mkdirs()
        cacheFile.createNewFile()
    }
    cacheFile.writeText(jacksonObjectMapper().writeValueAsString(Versioned(INTERFACE_SCAN_VERSION, newCache)))

    return interfaces

}

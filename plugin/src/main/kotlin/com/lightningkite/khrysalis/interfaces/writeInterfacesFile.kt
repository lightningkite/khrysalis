package com.lightningkite.khrysalis.interfaces

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.INTERFACE_SCAN_VERSION
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.Versioned
import com.lightningkite.khrysalis.utils.checksum
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File

fun writeInterfacesFile(sources: Sequence<File>, out: File, relativeTo: File): Map<String, InterfaceListener.InterfaceData> {
    val interfaces = HashMap<String, InterfaceListener.InterfaceData>()

    val existingCache: Map<String, FileCache> = if (out.exists()) {
        val versioned = jacksonObjectMapper().readValue<Versioned<Map<String, FileCache>>>(out)
        if (versioned.version == INTERFACE_SCAN_VERSION) versioned.value else mapOf()
    } else
        mapOf()
    val newCache = HashMap<String, FileCache>()

    sources.forEach { file ->
        val relativePath = file.toRelativeString(relativeTo)
        val hash = file.checksum()
        val existing = existingCache[relativePath]
        val cache = if (existing != null && existing.hash == hash) {
            existing
        } else {
            file.inputStream().buffered().use { stream ->
                log("Reading interfaces from: $file")
                val lexer = KotlinLexer(ANTLRInputStream(stream))
                val tokenStream = CommonTokenStream(lexer)
                val parser = KotlinParser(tokenStream)

                val listener = InterfaceListener(parser)
                ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())
                FileCache(
                    path = relativePath,
                    hash = hash,
                    data = listener.interfaces
                )
            }
        }
        newCache[cache.path] = cache

        for (i in cache.data) {
            interfaces[i.qualifiedName] = i
        }

    }

    if (!out.exists()) {
        out.parentFile.mkdirs()
        out.createNewFile()
    }
    out.writeText(jacksonObjectMapper().writeValueAsString(Versioned(INTERFACE_SCAN_VERSION, newCache)))

    return interfaces

}

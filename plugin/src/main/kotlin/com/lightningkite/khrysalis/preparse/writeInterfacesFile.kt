package com.lightningkite.khrysalis.preparse

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.INTERFACE_SCAN_VERSION
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.Versioned
import com.lightningkite.khrysalis.utils.checksum
import com.lightningkite.khrysalis.utils.forEachMultithreaded
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File

fun preparseKotlinFiles(sources: Sequence<File>, out: File, relativeTo: File): PreparseData {
    val interfaces = HashMap<String, InterfaceData>()
    val declarations = HashMap<String, String>()

    val existingCache: Map<String, FileCache> = if (out.exists()) {
        try {
            val versioned = jacksonObjectMapper().readValue<Versioned<Map<String, FileCache>>>(out)
            if (versioned.version == INTERFACE_SCAN_VERSION) versioned.value else mapOf<String, FileCache>()
        } catch(e:Exception){
            e.printStackTrace()
            mapOf<String, FileCache>()
        }
    } else
        mapOf<String, FileCache>()
    val newCache = HashMap<String, FileCache>()

    sources.forEachMultithreaded { file ->
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


                val listener = PreparseListener()
                listener.parse(relativePath.removeSuffix(".kt"), parser.kotlinFile())
                FileCache(
                    path = relativePath,
                    hash = hash,
                    data = PreparseData(
                        interfaces = listener.interfaces.associateBy { it.qualifiedName },
                        declarations = listener.topLevelDeclarations
                    )
                )
            }
        }
        newCache[cache.path] = cache

        interfaces.putAll(cache.data.interfaces)
        declarations.putAll(cache.data.declarations)

    }

    if (!out.exists()) {
        out.parentFile.mkdirs()
        out.createNewFile()
    }
    out.writeText(jacksonObjectMapper().writeValueAsString(Versioned(INTERFACE_SCAN_VERSION, newCache)))

    return PreparseData(
        interfaces = interfaces,
        declarations = declarations
    )

}

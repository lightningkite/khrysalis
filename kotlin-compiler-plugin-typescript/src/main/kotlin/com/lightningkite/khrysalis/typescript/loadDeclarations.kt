package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

fun loadDeclarations(files: Sequence<File>, local: File, into: DeclarationManifest){
    files
        .flatMap { it.walkTopDown() }
        .filter {
            it.name.endsWith(".ts")
        }
        .forEach { actualFile ->
            val decls = try {
                actualFile.useLines { lines ->
                    lines.filter { it.startsWith(declaresPrefix) }
                        .map { it.removePrefix(declaresPrefix) }
                        .toList()
                }
            } catch (t: Throwable) {
                throw IllegalArgumentException("Failed to parse TS/KT declarations from $actualFile.", t)
            }
            if(decls.isEmpty()) return@forEach
            if(actualFile.absoluteFile.startsWith(local.absoluteFile)) {
                val r = actualFile.relativeTo(local)
                for(decl in decls) {
                    into.local[decl] = r
                }
            } else {
                val r = actualFile.relativeTo(local.parentFile.resolve("node_modules"))
                for(decl in decls) {
                    into.node[decl] = r
                }
            }
        }
}
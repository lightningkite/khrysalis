package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import java.io.File

class DeclarationManifest(
    val node: MutableMap<String, File> = HashMap(),
    val local: MutableMap<String, File> = HashMap()
) {
    fun importLine(currentRelativeFile: File, fqName: String, name: String): TypescriptImport? {
        return local[fqName]?.let { relFile ->
            if (currentRelativeFile == relFile) {
                null
            } else {
                TypescriptImport(
                    path = "./"
                        .plus(
                            currentRelativeFile.parentFile?.let { p -> relFile.relativeTo(p).path } ?: relFile.path
                        )
                        .removeSuffix(".ts")
                        .let {
                            if (it.startsWith("./../")) "../" + it.removePrefix("./../")
                            else it
                        },
                    identifier = name
                )
            }
        } ?: node[fqName]?.let {
            TypescriptImport(it.path.removeSuffix(".ts"), name)
        }
    }

    fun load(files: Sequence<File>, local: File){
        files
            .flatMap { it.walkTopDown() }
            .filter {
                it.isFile && it.name.endsWith(".ts")
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
                        this.local[decl] = r
                    }
                } else {
                    val r = actualFile.absoluteFile.relativeTo(local.parentFile.resolve("node_modules").absoluteFile)
                    val shiftedPath = File(r.path.replace('\\', '/').replace("/src/", "/dist/"))
                    for(decl in decls) {
                        this.node[decl] = shiftedPath
                    }
                }
            }
    }

    companion object {
        fun load(files: Sequence<File>, local: File): DeclarationManifest{
            return DeclarationManifest().apply { load(files, local) }
        }
    }
}
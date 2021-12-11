package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import java.io.File

class DeclarationManifest(
    val node: MutableMap<String, String> = HashMap(),
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
            TypescriptImport(it, name)
        }
    }

    fun load(local: File){
        local.walkTopDown()
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
                val r = actualFile.relativeTo(local)
                for(decl in decls) {
                    this.local[decl] = r
                }
            }
    }

    fun loadNonlocal(files: List<File>){
        files
            .flatMap { it.walkTopDown() }
            .filter { it.name.endsWith("fqnames.txt", true) }
            .forEach {
                val lines = it.readLines().filter { it.isNotBlank() }
                val name = lines.first()
                lines.drop(1).forEach {
                    this.node[it] = name
                }
            }
    }

}
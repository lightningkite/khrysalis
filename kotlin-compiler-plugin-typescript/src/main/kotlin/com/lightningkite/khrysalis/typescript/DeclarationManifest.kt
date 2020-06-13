package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import java.io.File

class DeclarationManifest(
    val node: MutableMap<String, File> = HashMap(),
    val local: MutableMap<String, File> = HashMap()
) {
    fun importLine(currentRelativeFile: File, fqName: String, name: String): TemplatePart.Import? {
        return local[fqName]?.let { relFile ->
            if (currentRelativeFile == relFile) {
                null
            } else {
                TemplatePart.Import(
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
            TemplatePart.Import(it.path.removeSuffix(".ts"), name)
        }
    }

    fun load(files: Sequence<File>, local: File){
        files.forEach { println("Looking for declarations in $it") }
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
//                println("Found declarations in ${actualFile}")
                if(actualFile.absoluteFile.startsWith(local.absoluteFile)) {
                    val r = actualFile.relativeTo(local)
                    for(decl in decls) {
                        this.local[decl] = r
                    }
                } else {
                    val r = actualFile.relativeTo(local.parentFile.resolve("node_modules"))
                    val shiftedPath = File(r.path.replace("/src/", "/dist/"))
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
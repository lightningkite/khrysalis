package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.outputRelativePath
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import com.lightningkite.khrysalis.util.unixPath
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class DeclarationManifest(
    val outputDirectory: File,
    val commonPackage: String?,
    val node: MutableMap<String, String> = HashMap(),
    val local: MutableMap<String, File> = HashMap()
) {
    fun importLine(from: KtFile, fqName: String, name: String): TypescriptImport? {
        val fromPackageFile = outputDirectory.resolve(from.outputRelativePath(commonPackage, "ts"))
        return local[fqName]?.let { relFile ->
            if (fromPackageFile == relFile) {
                null
            } else {
                TypescriptImport(
                    path = "./"
                        .plus(
                            relFile.relativeTo(
                                fromPackageFile.parentFile ?: File(".")
                            ).unixPath.removeSuffix(".ts")
                        )
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

    fun loadLocal(local: File) {
        local.walkTopDown()
            .filter {
                it.isFile && it.name.endsWith(".ts") && !it.name.endsWith(".d.ts")
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
                if (decls.isEmpty()) return@forEach
                for (decl in decls) {
                    println("Local $decl found in $actualFile")
                    this.local[decl] = actualFile
                }
            }
    }

}
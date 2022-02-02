package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.outputRelativePath
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class DeclarationManifest(
    val commonPackage: String?,
    val node: MutableMap<String, String> = HashMap(),
    val local: MutableMap<String, String> = HashMap()
) {
    fun importLine(from: KtFile, fqName: String, name: String): TypescriptImport? {
        val fromPackageFile = from.outputRelativePath(commonPackage, "ts")
        return local[fqName]?.let { relFile ->
            if (fromPackageFile == relFile) {
                null
            } else {
                TypescriptImport(
                    path = "./"
                        .plus(
                            File(relFile).relativeTo(File(fromPackageFile).parentFile ?: File(".")).path.removeSuffix(".ts")
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
                    this.local[decl] = r.path
                }
            }
    }

    fun loadNonlocal(files: List<File>, filterOut: File){
        files
            .flatMap { it.walkTopDown() }
            .filter { it.name.endsWith("ts.fqnames", true) }
            .filter {
                println("Checking $it against $filterOut")
                !it.startsWith(filterOut)
            }
            .forEach {
                val lines = it.readLines().filter { it.isNotBlank() }
                val name = lines.firstOrNull() ?: return@forEach
                lines.drop(1).forEach {
                    this.node[it] = name
                }
            }
    }

}
package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import com.lightningkite.khrysalis.util.unixPath
import java.io.File

fun renderImports(projectName: String?, relPath: String, imports: Collection<TypescriptImport>, writer: Appendable){
    imports
        .distinctBy { it.asName ?: it.identifier }
        .groupBy {
            val path = it.path
            if(path.contains('|')) {
                val nodePackage = path.substringBefore('|')
                val pathInside = path.substringAfter('|')
                if(nodePackage == projectName) {
                    val rel = "./".plus(File(pathInside).absoluteFile.relativeTo(File(relPath).absoluteFile.parentFile).unixPath)
                    if(rel.startsWith("./.."))
                        rel.removePrefix("./")
                    else
                        rel
                } else {
                    nodePackage
                }
            } else {
                path
            }
        }
        .mapValues { it.value.sortedBy { it.identifier } }
        .entries.sortedBy { it.key }
        .forEach { (path, parts) ->
        if (parts.size == 1 && parts.first().identifier == TypescriptImport.WHOLE) {
            writer.append("import ")
            writer.append(parts.first().identifier)
            writer.append(" from '")
            writer.append(path)
            writer.appendln("'")
        } else if (parts.size == 1 && parts.first().identifier == "") {
            writer.append("import {} from '")
            writer.append(path)
            writer.appendln("'")
        } else {
            writer.append("import { ")
            writer.append(parts.sortedBy { it.asName ?: it.identifier }.joinToString(", ") {
                it.asName?.takeUnless { n -> n == it.identifier }?.let { name ->
                    it.identifier + " as " + name
                } ?: it.identifier
            })
            writer.append(" } from '")
            writer.append(path)
            writer.appendln("'")
        }
    }
}
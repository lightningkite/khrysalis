package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import java.io.File

fun renderImports(projectName: String?, relPath: String, imports: Collection<TypescriptImport>, writer: Appendable){
    imports.distinctBy { it.asName ?: it.identifier }.groupBy { it.path }.forEach { (path, parts) ->
        val usePath = (projectName?.let { p ->
            val prefix = "$p/dist/"
            if (path.startsWith(prefix, true)) {
                val rel = "./".plus(File(path.drop(prefix.length)).absoluteFile.relativeTo(File(relPath).absoluteFile.parentFile).path)
                if(rel.startsWith("./.."))
                    rel.removePrefix("./")
                else
                    rel
            } else {
                path
            }
        } ?: path).replace("\\", "/")
        if (parts.size == 1 && parts.first().identifier == TypescriptImport.WHOLE) {
            writer.append("import ")
            writer.append(parts.first().identifier)
            writer.append(" from '")
            writer.append(usePath)
            writer.appendln("'")
        } else if (parts.size == 1 && parts.first().identifier == "") {
            writer.append("import {} from '")
            writer.append(usePath)
            writer.appendln("'")
        } else {
            writer.append("import { ")
            writer.append(parts.sortedBy { it.asName ?: it.identifier }.joinToString(", ") {
                it.asName?.takeUnless { n -> n == it.identifier }?.let { name ->
                    it.identifier + " as " + name
                } ?: it.identifier
            })
            writer.append(" } from '")
            writer.append(usePath)
            writer.appendln("'")
        }
    }
}
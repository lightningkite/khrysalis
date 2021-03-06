package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import java.io.File

fun renderImports(projectName: String?, relPath: String, imports: Collection<TemplatePart.Import>, writer: Appendable){
    imports.distinctBy { it.asName ?: it.identifier }.groupBy { it.path }.forEach { (path, parts) ->
        val usePath = projectName?.let { p ->
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
        } ?: path
        if (parts.size == 1 && parts.first().identifier == TemplatePart.Import.WHOLE) {
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
                it.asName?.let { name ->
                    it.identifier + " as " + name
                } ?: it.identifier
            })
            writer.append(" } from '")
            writer.append(usePath)
            writer.appendln("'")
        }
    }
}
package com.lightningkite.khrysalis.typescript

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
}
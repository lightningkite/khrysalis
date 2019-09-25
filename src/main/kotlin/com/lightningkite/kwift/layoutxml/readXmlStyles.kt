package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.utils.camelCase
import net.jodah.xsylum.Xsylum
import java.io.File

private data class IntermediateStyle(val parent: String? = null, val parts: Map<String, String> = mapOf())

fun File.readXMLStyles(): Map<String, Map<String, String>> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "style" }
        .associate {
            val name = (it.attributes["name"] ?: "noname")
            val map = it.children.associate {
                (it.attributes["name"] ?: "noname") to it.element.textContent
            }
            val parent = it.attributes["parent"]
            name to IntermediateStyle(parent, map)
        }
        .let {
            it.mapValues { entry ->
                val complete = HashMap<String, String>()
                var current: IntermediateStyle? = entry.value
                while(current != null) {
                    complete += current.parts
                    current = current.parent?.let { p -> it[p] }
                }
                complete
            }
        }
}

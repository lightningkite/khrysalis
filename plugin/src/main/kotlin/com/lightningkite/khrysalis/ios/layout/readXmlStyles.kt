package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.XmlNode
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
            val parent = it.attributes["parent"]?.removePrefix("@style/")
            name to IntermediateStyle(parent, map)
        }
        .let {
            it.mapValues { entry ->
                val complete = HashMap<String, String>()
                var current: IntermediateStyle? = entry.value
                while(current != null) {
                    for((key, value) in current.parts) {
                        if(!complete.containsKey(key)) {
                            complete[key] = value
                        }
                    }
                    current = current.parent?.let { p -> it[p] }
                }
                complete
            }
        }
}

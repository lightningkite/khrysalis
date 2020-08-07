package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.XmlNode
import java.io.File

private data class IntermediateStyle(val parent: String? = null, val parts: Map<String, String> = mapOf())

fun File.readXMLStyles(): Map<String, Map<String, String>> {
    if(!this.exists()) {
        println("WARNING: Could not find styles file at '${this}'!")
        return mapOf()
    }
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "style" }
        .associate {
            val name = (it.allAttributes["name"] ?: "noname")
            val map = it.children.associate {
                (it.allAttributes["name"] ?: "noname") to it.element.textContent
            }
            val parent = it.allAttributes["parent"]?.removePrefix("@style/") ?: if(name != "AppTheme") "AppTheme" else null
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
                    val next = current.parent?.let { p -> it[p] }
                    if(next == current) break
                    current = next
                }
                complete
            }
        }
}

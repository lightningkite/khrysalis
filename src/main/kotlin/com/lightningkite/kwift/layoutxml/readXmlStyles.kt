package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.utils.camelCase
import net.jodah.xsylum.Xsylum
import java.io.File

fun File.readXMLStyles(): Map<String, Map<String, String>> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "style" }
        .associate {
            (it.attributes["name"] ?: "noname") to it.children.associate {
                (it.attributes["name"] ?: "noname") to it.element.textContent
            }
        }
}

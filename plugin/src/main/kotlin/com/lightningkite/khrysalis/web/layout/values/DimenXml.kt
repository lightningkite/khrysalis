package com.lightningkite.khrysalis.web.layout.values

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.asCssDimension
import com.lightningkite.khrysalis.web.useScssVariables
import java.io.File

fun translateXmlDimensionsToCss(file: File, out: Appendable) {
    if (!useScssVariables) out.appendLine("* {")
    XmlNode.read(file, mapOf())
        .children
        .asSequence()
        .filter { it.name == "dimen" }
        .associate {
            val name = (it.allAttributes["name"] ?: "noname").kabobCase()
            val size = it.element.textContent.asCssDimension() ?: "0px"
            name to size
        }
        .let { map ->
            fun determineValue(value: String): Int {
                return if (value.startsWith("$")) {
                    1 + (map[value.drop(1)]?.let { determineValue(it) } ?: 0)
                } else 0
            }
            map.entries.sortedBy { (key, value) -> determineValue(value) }
        }
        .forEach { (name, color) ->
            if (useScssVariables) {
                out.appendLine("\$dimen-$name: $color;")
            } else {
                out.appendLine("--dimen-$name: $color;")
            }
        }
    if (!useScssVariables) out.appendLine("}")
}

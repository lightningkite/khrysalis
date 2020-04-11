package com.lightningkite.khrysalis.web.layout.values

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.asCssColor
import com.lightningkite.khrysalis.web.useScssVariables
import java.io.File
import java.lang.Appendable


fun translateXmlColorsToCss(file: File, out: Appendable) {
    val ignored = setOf("white", "black", "transparent")
    if(!useScssVariables) out.appendln("* {")
    XmlNode.read(file, mapOf())
        .children
        .asSequence()
        .filter { it.name == "color" }
        .filter { it.allAttributes["name"] !in ignored }
        .associate {
            val color = it.element.textContent.asCssColor() ?: "#000"
            val name = (it.allAttributes["name"] ?: "noname").kabobCase()
            name to color
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
            if(useScssVariables){
                out.appendln("\$color-$name: $color;")
            } else {
                out.appendln("--color-$name: $color;")
            }
        }
    if(!useScssVariables) out.appendln("}")
}

fun translateXmlColorSetToCss(file: File, out: Appendable) {
    val name = file.nameWithoutExtension.kabobCase()
    out.appendln("/*$name*/")
    XmlNode.read(file, mapOf()).children.forEach { subnode ->
        var conditions = ""
        subnode.attributeAsBoolean("android:state_enabled")?.let {
            conditions += if (it) ":enabled" else ":disabled"
        }
        subnode.attributeAsBoolean("android:state_pressed")?.let {
            conditions += if (it) ":active:hover" else ":not(:active:hover)"
        }
        subnode.attributeAsBoolean("android:state_selected")?.let {
            conditions += if (it) ":checked" else ":not(:checked)"
        }
        subnode.attributeAsBoolean("android:state_focused")?.let {
            conditions += if (it) ":focus" else ":not(:focus)"
        }
        subnode.attributeAsBoolean("android:state_checked")?.let {
            conditions += if (it) ":checked" else ":not(:checked)"
        }
        subnode.allAttributes["android:color"]?.let { raw ->
            val color = raw.asCssColor()
            if (conditions.isEmpty()) {
                out.appendln("* {")
            } else {
                out.appendln("$conditions {")
            }
            out.appendln("--color-$name: $color;")
            out.appendln("}")
        }
    }
}

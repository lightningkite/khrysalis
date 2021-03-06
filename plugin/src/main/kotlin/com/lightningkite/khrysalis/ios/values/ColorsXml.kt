package com.lightningkite.khrysalis.ios.values

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import java.io.File
import java.lang.Appendable


fun File.translateXMLColors(out: Appendable) {
    val ignored = setOf("white", "black", "transparent")
    XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "color" }
        .filter { it.allAttributes["name"] !in ignored }
        .forEach {
            val raw = it.element.textContent
            val name = (it.allAttributes["name"] ?: "noname").safeSwiftIdentifier()
            val color = when{
                raw.startsWith("@color/") -> {
                    val colorName = raw.removePrefix("@color/")
                    "R.color.${colorName}"
                }
                raw.startsWith("@android:color/") -> {
                    val colorName = raw.removePrefix("@android:color/")
                    "R.color.${colorName}"
                }
                raw.startsWith("#") -> {
                    raw.hashColorToUIColor()
                }
                else -> "UIColor.black"
            }
            out.appendln("static let $name: UIColor = $color")
        }
}

fun File.translateXmlColorSet(out: Appendable) {
    out.appendln("static func ${nameWithoutExtension.safeSwiftIdentifier()}(_ state: UIControl.State) -> UIColor {")
    XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "item" }
        .forEach { subnode ->

            val conditions = ArrayList<String>()
            subnode.attributeAsBoolean("android:state_enabled")?.let {
                conditions += (if(it) "!" else "") + "state.contains(.disabled)"
            }
            subnode.attributeAsBoolean("android:state_pressed")?.let {
                conditions += (if(it) "" else "!") + "state.contains(.highlighted)"
            }
            subnode.attributeAsBoolean("android:state_selected")?.let {
                conditions += (if(it) "" else "!") + "state.contains(.selected)"
            }
            subnode.attributeAsBoolean("android:state_focused")?.let {
                conditions += (if(it) "" else "!") + "state.contains(.focused)"
            }
            subnode.attributeAsBoolean("android:state_checked")?.let {
                conditions += (if(it) "" else "!") + "state.contains(.selected)"
            }

            if(conditions.isEmpty()) {
                out.appendln("return ${subnode.attributeAsSwiftColor("android:color")}")
            } else {
                out.appendln("if ${conditions.joinToString(" && ")} {")
                out.appendln("return ${subnode.attributeAsSwiftColor("android:color")}")
                out.appendln("}")
            }
        }
    out.appendln("return UIColor.white")
    out.appendln("}")
}

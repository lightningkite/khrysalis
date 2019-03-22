package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.utils.camelCase
import java.io.File


fun File.readXMLColors(): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "color" }
        .associate {
            (it.attributes["name"] ?: "noname") to it.element.textContent.hashColorToUIColor()
        }
}

fun File.readXMLColorSet(colors: Map<String, String>): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "item" }
        .associate {
            val raw = it.attributes["android:color"]
            val fixed = when {
                raw == null -> null
                raw.startsWith("#") -> raw.hashColorToUIColor()
                raw.startsWith("@color/") -> colors[raw.removePrefix("@color/")]
                raw.startsWith("@android:color/") -> colors[raw.removePrefix("@android:color/")]
                else -> null
            }
            val state = it.attributes.entries.find { it.value == "true" }?.key?.let {
                when(it){
                    "state_checked" -> ".selected"
                    "state_selected" -> ".selected"
                    "state_focused" -> ".focused"
                    else -> null
                }
            } ?: it.attributes.entries.find { it.value == "false" }?.key?.let {
                when(it){
                    "state_enabled" -> ".disabled"
                    else -> null
                }
            } ?: ".normal"
//            (it.attributes["android:color"] ?: "noname") to it.element.textContent.hashColorToUIColor()
            state to (fixed ?: "UIColor.black")
        }
}


fun Map<String, String>.writeXMLColors(): String {
    return buildString {
        appendln("//")
        appendln("// ResourcesColors.swift")
        appendln("// Created by Kwift")
        appendln("//")
        appendln("")
        appendln("import Foundation")
        appendln("import UIKit")
        appendln("")
        appendln("")
        appendln("public enum ResourcesColors {")
        appendln("    static let transparent = UIColor.clear")
        appendln("    static let black = UIColor.black")
        appendln("    static let darkerGray = UIColor.darkGray")
        appendln("    static let darkGray = UIColor.darkGray")
        appendln("    static let gray = UIColor.gray")
        appendln("    static let lightGray = UIColor.lightGray")
        appendln("    static let lighterGray = UIColor.lightGray")
        appendln("    static let white = UIColor.white")
        for((key, value) in this@writeXMLColors.entries){
            appendln("    static let ${key.camelCase()} = $value")
        }
        appendln("}")
    }
}

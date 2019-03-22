package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.utils.camelCase
import java.io.File


fun File.readXMLStrings(): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "string" }
        .associate {
            (it.attributes["name"] ?: "noname") to it.element.textContent
        }
}

fun Map<String, String>.writeXMLStrings(): String {
    return buildString {
        appendln("//")
        appendln("// ResourcesStrings.swift")
        appendln("// Created by Kwift")
        appendln("//")
        appendln("")
        appendln("import Foundation")
        appendln("import UIKit")
        appendln("")
        appendln("")
        appendln("public enum ResourcesStrings {")
        for((key, value) in this@writeXMLStrings.entries){
            val fixedString = value
                .replace("\\", "'")
                .replace(Regex("\n *"), " ")
            appendln("    static let ${key.camelCase()} = NSLocalizedString(\"$fixedString\", comment: \"$key\")")
        }
        appendln("}")
    }
}

package com.lightningkite.kwift.values

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase
import java.io.File


fun File.readXMLDimen(): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "dimen" }
        .associate {
            (it.attributes["name"] ?: "noname") to it.element.textContent.filter { it.isDigit() }
        }
}


fun Map<String, String>.writeXMLDimen(): String {
    return buildString {
        appendln("//")
        appendln("// ResourcesDimensions.swift")
        appendln("// Created by Kwift")
        appendln("//")
        appendln("")
        appendln("import Foundation")
        appendln("import UIKit")
        appendln("import Kwift")
        appendln("")
        appendln("")
        appendln("public enum ResourcesDimensions {")
        for((key, value) in this@writeXMLDimen.entries){
            if(key.contains("programmatic", true)){
                appendln("    static var ${key.camelCase()}: CGFloat = $value")
            } else {
                appendln("    static let ${key.camelCase()}: CGFloat = $value")
            }
        }
        appendln("}")
    }
}

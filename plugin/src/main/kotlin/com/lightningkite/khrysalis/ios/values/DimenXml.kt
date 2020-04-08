package com.lightningkite.khrysalis.ios.values

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File


fun File.readXMLDimen(): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "dimen" }
        .associate {
            (it.allAttributes["name"] ?: "noname") to it.element.textContent.filter { it.isDigit() || it == '.' || it == '-' }
        }
}


fun Map<String, String>.writeXMLDimen(): String {
    return buildString {
        appendln("//")
        appendln("// ResourcesDimensions.swift")
        appendln("// Created by Khrysalis")
        appendln("//")
        appendln("")
        appendln("import Foundation")
        appendln("import UIKit")
        appendln("import Khrysalis")
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

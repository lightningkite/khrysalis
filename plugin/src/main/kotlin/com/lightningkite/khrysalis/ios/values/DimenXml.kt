package com.lightningkite.khrysalis.ios.values

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File
import java.lang.Appendable


fun File.readXMLDimen(): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "dimen" }
        .associate {
            (it.allAttributes["name"]
                ?: "noname") to it.element.textContent.filter { it.isDigit() || it == '.' || it == '-' }
        }
}


fun Map<String, String>.writeXMLDimen(out: Appendable) {
    for ((key, value) in this@writeXMLDimen.entries) {
        if (key.contains("programmatic", true)) {
            out.appendln("static var ${key}: CGFloat = $value")
        } else {
            out.appendln("static let ${key}: CGFloat = $value")
        }
    }
}

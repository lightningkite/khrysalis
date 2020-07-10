package com.lightningkite.khrysalis.ios.values

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File


fun File.readXMLStrings(): Map<String, String> {
    return XmlNode.read(this, mapOf())
        .children
        .asSequence()
        .filter { it.name == "string" }
        .associate {
            (it.allAttributes["name"] ?: "noname") to it.element.textContent
        }
}

fun Map<String, String>.writeXMLStringsTranslation(base: Map<String, String>, locale: String): String {
    return buildString {
        appendln("/*")
        appendln("Translation to locale $locale")
        appendln("Automatically written by Khrysalis")
        appendln("*/")
        appendln("")
        for((key, value) in this@writeXMLStringsTranslation.entries){
            val baseString = (base[key] ?: error("No matching key in base language"))
                .replace("\\'", "'")
                .replace("\\$", "$")
                .replace(Regex("\n *"), " ")
            val fixedString = value
                .replace("\\'", "'")
                .replace("\\$", "$")
                .replace(Regex("\n *"), " ")
            appendln("\"$baseString\" = \"$fixedString\";")
        }
    }
}

fun Map<String, String>.writeXMLStrings(out: Appendable) {
    for((key, value) in this@writeXMLStrings.entries){
        val fixedString = value
            .replace("\\'", "'")
            .replace("\\$", "$")
            .replace(Regex("\n *"), " ")
        out.appendln("    static let ${key.camelCase()} = NSLocalizedString(\"$fixedString\", comment: \"$key\")")
    }
}

package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.hashColorToUIColor
import java.io.File

data class OngoingLayoutConversion(
    val appendable: Appendable,
    val resourcesDirectory: File,
    val styles: Styles,
    val converter: LayoutConverter = LayoutConverter.normal,
    val bindings: HashMap<String, String> = HashMap(),
    val delegateBindings: HashMap<String, String> = HashMap(),
    val sublayouts: HashMap<String, String> = HashMap()
) : Appendable by appendable {

    val colorSets = resourcesDirectory.resolve("color").listFiles()?.map { it.nameWithoutExtension }?.toSet() ?: setOf()
    var controlIndex: Int = 0

    fun construct(node: XmlNode) {
        if (node.name == "include") {
            val className = node.allAttributes["layout"]!!.removePrefix("@layout/").camelCase().capitalize().plus("Xml")
            val id = node.allAttributes["android:id"]?.removePrefix("@id/")?.removePrefix("@+id/")?.camelCase()
            if (id != null) {
                sublayouts[id] = className
                append("${id.safeSwiftIdentifier()}.setup(dependency: dependency)")
            } else {
                append("$className().setup(dependency: dependency)")
            }
        } else if (node.name in converter.skipTypes) {
            construct(node.children.first())
        } else {
            append((converter.viewTypes[node.name] ?: ViewType.default(node)).iosConstructor)
        }
    }

    fun writeSetup(node: XmlNode) {
        if (node.name == "include") {
            //Do nothing
        } else if (node.name in converter.skipTypes) {
            writeSetup(node.children.first())
        } else {
            (converter.viewTypes[node.name] ?: ViewType.default(node)).writeConfiguration(this, node)
        }
    }

}


fun Appendable.setToColor(node: XmlNode, key: String, controlView: String = "view", write: (color: String, state: String) -> Unit): Boolean {
    val raw = node.allAttributes[key] ?: return false
    return when {
        raw.startsWith("@color/") || raw.startsWith("@android:color/") -> {
            val colorName = raw.removePrefix("@color/").removePrefix("@android:color/")
            appendLine("applyColor($controlView, R.color.${colorName}) { (c, s) in")
            write("c", "s")
            appendLine("}")
            true
        }
        raw.startsWith("#") -> {
            write(raw.hashColorToUIColor(), ".normal")
            true
        }
        else -> {
            false
        }
    }
}
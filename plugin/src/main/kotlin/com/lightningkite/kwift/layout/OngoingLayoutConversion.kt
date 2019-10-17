package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase
import com.lightningkite.kwift.utils.hashColorToUIColor
import java.io.File

data class OngoingLayoutConversion(
    val appendable: Appendable,
    val layoutsDirectory: File,
    val styles: Styles,
    val converter: LayoutConverter = LayoutConverter.normal,
    val bindings: HashMap<String, String> = HashMap(),
    val sublayouts: HashMap<String, String> = HashMap()
) : Appendable by appendable {

    val colorSets = layoutsDirectory.resolve("../color").listFiles()?.map { it.nameWithoutExtension }?.toSet() ?: setOf()
    var controlIndex: Int = 0
    fun XmlNode.setToColorGivenControl(key: String, controlView: String = "view", write: (color: String) -> Unit): Boolean {
        val raw = this.attributes[key] ?: return false
        return when {
            raw.startsWith("@color/") || raw.startsWith("@android:color/") -> {
                val colorName = raw.removePrefix("@color/").removePrefix("@android:color/")
                if (colorName !in colorSets) {
                    write("ResourcesColors.${colorName.camelCase()}")
                } else {
                    write("ResourcesColors.${colorName.camelCase()}($controlView.state)")
                    appendln("let _ = $controlView.addOnStateChange(retainer: $controlView, id: ${controlIndex++}, action: { state in")
                    write("ResourcesColors.${colorName.camelCase()}(state)")
                    appendln("})")
                }
                true
            }
            raw.startsWith("#") -> {
                write(raw.hashColorToUIColor())
                true
            }
            else -> {
                false
            }
        }
    }

    fun construct(node: XmlNode) {
        if (node.name == "include") {
            val className = node.attributes["layout"]!!.removePrefix("@layout/").camelCase().capitalize().plus("Xml")
            val id = node.attributes["android:id"]?.removePrefix("@id/")?.removePrefix("@+id/")?.camelCase()
            if (id != null) {
                sublayouts[id] = className
                append("$id.setup(dependency)")
            } else {
                append("$className().setup(dependency)")
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

    /*
    fun write(node: XmlNode) {
        if (node.name == "include") {
            val className = node.attributes["layout"]!!.removePrefix("@layout/").camelCase().capitalize().plus("Xml")
            val id = node.attributes["android:id"]?.removePrefix("@id/")?.removePrefix("@+id/")?.camelCase()
            appendln("{ () -> UIView in ")
            appendln("let subxml = $className()")
            appendln("let view = subxml.setup(dependency)")
            if(id != null){
                bindings[id] = className
                appendln("self.${id}_raw = subxml")
            }
            appendln("return view")
            appendln("}()")
        } else if (node.name in converter.skipTypes) {
            for (child in node.children) {
                write(child)
            }
        } else {
            (converter.viewTypes[node.name] ?: ViewType.default(node)).write(this, node)
        }
    }*/
}


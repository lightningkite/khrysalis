package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase
import java.io.File

data class OngoingLayoutConversion(
    val appendable: Appendable,
    val resourcesDirectory: File,
    val styles: Styles,
    val converter: LayoutConverter = LayoutConverter.normal,
    val bindings: HashMap<String, String> = HashMap(),
    val sublayouts: HashMap<String, String> = HashMap()
): Appendable by appendable {
    fun construct(node: XmlNode) {
        if (node.name == "include") {
            val className = node.attributes["layout"]!!.removePrefix("@layout/").camelCase().capitalize().plus("Xml")
            val id = node.attributes["android:id"]?.removePrefix("@id/")?.removePrefix("@+id/")?.camelCase()
            if(id != null){
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


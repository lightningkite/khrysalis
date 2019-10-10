package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase
import java.io.File

data class OngoingLayoutConversion(
    val appendable: Appendable,
    val resourcesDirectory: File,
    val styles: Styles,
    val converter: LayoutConverter = LayoutConverter.normal,
    val bindings: HashMap<String, String> = HashMap()
): Appendable by appendable {
    fun write(node: XmlNode) {
        if (node.name == "include") {
            val className = node.attributes["layout"]!!.removePrefix("@layout/").camelCase().capitalize().plus("Xml")
            val id = node.attributes["android:id"]?.removePrefix("@id/")?.removePrefix("@+id/")?.camelCase()
            appendln("{ () -> UIView in ")
            appendln("let subxml = $className()")
            appendln("let view = subxml.setup(dependency)")
            if(id != null){
                bindings[id] = className
                appendln("self.${id} = subxml")
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


package com.lightningkite.khrysalis.utils

import com.lightningkite.khrysalis.generic.VirtualType
import com.lightningkite.khrysalis.ios.layout.Styles
import org.w3c.dom.Node
import java.io.File
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory

class XmlNode(
    val element: Node,
    val styles: Styles,
    val directory: File,
    val baseDirectoryAlt: File? = null,
    val additionalAttributes: Map<String, String> = mapOf(),
    var parent: XmlNode? = null
): VirtualType {

    val tags = HashMap<String, String>(0)

    class Attribute(val parent: XmlNode, override val type: String, val value: String): VirtualType {
        override val parts: Iterable<Any> get() = listOf(value)
    }

    override val parts: Iterable<Attribute>
        get() = allAttributes.entries.map { it -> Attribute(this, it.key, it.value) }
    override val type: String
        get() = element.nodeValue

    fun attribute(key: String): Attribute? = allAttributes[key]?.let { Attribute(this, key, it) }

    val name get() = element.nodeName
    val allAttributes: Map<String, String> by lazy {
        if (!element.hasAttributes()) return@lazy mapOf<String, String>()
        val map = HashMap<String, String>()
        element.attributes.let { att ->
            (0 until att.length).associateTo(map) { att.item(it).let { it.nodeName to it.nodeValue } }
        }
        val style = map["style"]?.removePrefix("@style/")?.let { styleName ->
            styles[styleName]
        }
        if (style != null) {
            for ((k, v) in style) {
                if (!map.containsKey(k)) {
                    map[k] = v
                }
            }
        }
        element.attributes.let { att ->
            (0 until att.length).associateTo(map) { att.item(it).let { it.nodeName to it.nodeValue } }
        }
        additionalAttributes + map
    }
    val directAttributes: Map<String, String> by lazy {
        if (!element.hasAttributes()) return@lazy mapOf<String, String>()
        val map = HashMap<String, String>()
        element.attributes.let { att ->
            (0 until att.length).associateTo(map) { att.item(it).let { it.nodeName to it.nodeValue } }
        }
        element.attributes.let { att ->
            (0 until att.length).associateTo(map) { att.item(it).let { it.nodeName to it.nodeValue } }
        }
        additionalAttributes + map
    }
    val children by lazy {
        element.childNodes.let { att ->
            (0 until att.length).asSequence()
                .map { att.item(it) }
                .filter { it.nodeType == Node.ELEMENT_NODE }
                .map {
                    if(it.nodeName == "include"){
                        try {
                            val filename = it.attributes
                                ?.getNamedItem("layout")!!
                                .nodeValue
                                .removePrefix("@layout/")
                                .plus(".xml")
                            val fileOptionA = directory.resolve(filename)
                            val fileOptionB = baseDirectoryAlt?.resolve(filename)
                            val file = fileOptionA.takeIf { it.exists() }
                                ?: fileOptionB?.takeIf { it.exists() }
                                ?: throw IllegalArgumentException("Could not find file for layout $filename in $fileOptionA or $fileOptionB")
                            val node =
                                read(file, styles)
                            XmlNode(it, styles, directory, additionalAttributes = node.allAttributes, parent = this)
                        } catch(e:Exception){
                            e.printStackTrace()
                            XmlNode(it, styles, directory, parent = this)
                        }
                    } else {
                        XmlNode(it, styles, directory, parent = this)
                    }
                }
                .toList()
        }
    }


    companion object {
        fun read(file: File, styles: Styles, baseDirectoryAlt: File? = null): XmlNode {
            val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            return XmlNode(document.documentElement, styles, file.parentFile, baseDirectoryAlt)
        }
    }
}

fun XmlNode.attributeAsBoolean(key: String): Boolean? {
    val raw = allAttributes[key] ?: return null
    return when(raw.toLowerCase()) {
        "true" -> true
        "false" -> false
        else -> null
    }
}
fun XmlNode.attributeAsDouble(key: String): Double? {
    val raw = allAttributes[key] ?: return null
    return when {
        else -> raw.filter { it.isDigit() || it == '.' || it == '-'  }.toDoubleOrNull()
    }
}

fun XmlNode.attributeAsInt(key: String): String? {
    val raw = allAttributes[key] ?: return null
    return raw.filter { it.isDigit() || it == '-'  }.toIntOrNull()?.toString()
}

fun XmlNode.attributeAsStringKotlin(key: String): String? {
    val raw = allAttributes[key] ?: return null
    return when {
        raw.startsWith("@string/") -> "ResourcesStrings.${raw.removePrefix("@string/").camelCase()}"
        else -> "\"${raw.replace("$", "\\$")}\""
    }
}
fun XmlNode.attributeAsGravityKotlin(key: String): Int? {
    val raw = allAttributes[key]?.split('|') ?: return null
    var gravity = 0
    if(raw.contains("center"))
        gravity = gravity or 0x00000011
    if(raw.contains("center_horizontal"))
        gravity = gravity or 0x00000001
    if(raw.contains("left"))
        gravity = gravity or 0x00000003
    if(raw.contains("right"))
        gravity = gravity or 0x00000005
    if(raw.contains("center_vertical"))
        gravity = gravity or 0x00000010
    if(raw.contains("top"))
        gravity = gravity or 0x00000030
    if(raw.contains("bottom"))
        gravity = gravity or 0x00000050
    return gravity
}
object SafePaddingFlags {
    const val NONE: Int = 0
    const val TOP: Int = 1
    const val RIGHT: Int = 2
    const val BOTTOM: Int = 4
    const val LEFT: Int = 8
    const val ALL: Int = 15
}
fun XmlNode.attributeAsEdgeFlagsKotlin(key: String): Int? {
    val raw = allAttributes[key]?.split('|') ?: return null
    var gravity = 0
    if(raw.contains("center"))
        gravity = gravity or SafePaddingFlags.ALL
    if(raw.contains("all"))
        gravity = gravity or SafePaddingFlags.ALL
    if(raw.contains("center_horizontal"))
        gravity = gravity or SafePaddingFlags.LEFT or SafePaddingFlags.RIGHT
    if(raw.contains("horizontal"))
        gravity = gravity or SafePaddingFlags.LEFT or SafePaddingFlags.RIGHT
    if(raw.contains("left"))
        gravity = gravity or SafePaddingFlags.LEFT
    if(raw.contains("right"))
        gravity = gravity or SafePaddingFlags.RIGHT
    if(raw.contains("center_vertical"))
        gravity = gravity or SafePaddingFlags.TOP or SafePaddingFlags.BOTTOM
    if(raw.contains("vertical"))
        gravity = gravity or SafePaddingFlags.TOP or SafePaddingFlags.BOTTOM
    if(raw.contains("top"))
        gravity = gravity or SafePaddingFlags.TOP
    if(raw.contains("bottom"))
        gravity = gravity or SafePaddingFlags.BOTTOM
    return gravity
}

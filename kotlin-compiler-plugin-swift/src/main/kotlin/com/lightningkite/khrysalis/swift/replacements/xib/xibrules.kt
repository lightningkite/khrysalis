package com.lightningkite.khrysalis.swift.replacements.xib

import com.fasterxml.jackson.annotation.JsonProperty
import com.lightningkite.khrysalis.swift.replacements.*


val availableLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
private fun idChar() = availableLetters.random()
fun makeId(): String {
    return "${idChar()}${idChar()}${idChar()}-${idChar()}${idChar()}-${idChar()}${idChar()}${idChar()}"
}

class PureXmlOut(var name: String = "") {
    val attributes = HashMap<String, String>()
    val children = ArrayList<PureXmlOut>()
    fun getOrPutChild(name: String): PureXmlOut {
        children.find { it.name == name }?.let { return it }
        val newNode = PureXmlOut(name)
        children.add(newNode)
        return newNode
    }

    fun getOrPutChildKeyed(key: String, value: String, name: String): PureXmlOut {
        children.find { it.attributes[key] == name }?.let { return it }
        val newNode = PureXmlOut(name)
        children.add(newNode)
        return newNode
    }

    override fun toString(): String = buildString { write(this) }
    fun write(to: Appendable) = with(to) {
        append('<')
        append(name)
        append(' ')
        for ((key, value) in attributes) {
            append(key)
            append('=')
            append('"')
            append(
                value
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("&", "&amp;")
            )
            append('"')
            append(' ')
        }
        if (children.isEmpty()) {
            append("/>")
        } else {
            append('>')
            for (child in children) {
                append(child.toString())
            }
            append("</")
            append(name)
            append('>')
        }
    }
}

data class XibTranslation(
    val name: String,
    val deferTo: String,
    val defaults: Map<AttPath, String> = mapOf(),
    val attributes: Map<String, AttHandler> = mapOf()
)

data class AttPath(
    val pathType: AttPathType,
    val name: String,
    val type: String = name,
    val then: AttPath? = null
) {
    constructor(string: String) : this(
        pathType = AttPathType.values().find { it.name.equals(string.substringBefore('/'), true) }!!,
        name = string.substringAfter('/').substringBefore(':'),
        type = string.substringAfter('/').substringAfter(':').substringBefore('/'),
        then = string.substringAfter('/').substringAfter(':').substringAfter('/', "").takeUnless { it.isEmpty() }
            ?.let { AttPath(it) }
    )
}

data class AttHandler(
    val path: AttPath? = null,
    @JsonProperty("as") val asKind: AttKind? = null,
    val constant: String? = null,

    val mapValues: Map<String, Map<AttPath, String>>? = null,

    val multiple: List<AttHandler>? = null
) {
    val kind: AttHandlerKind
        get() = when {
            path != null -> AttHandlerKind.Direct
            multiple != null -> AttHandlerKind.Multiple
            mapValues != null -> AttHandlerKind.ValueMap
            else -> throw IllegalStateException()
        }
}

enum class AttPathType {
    Property, UserDefined, Attribute
}

enum class AttKind {
    Font, Dimension, Color, Number, Raw, String, Boolean
}

enum class AttHandlerKind {
    Direct, Multiple, ValueMap
}

//data class XibClassReference(
//    val name: String = "",
//    val module: String? = null
//) {
//    override fun toString(): String {
//        return if(module != null){
//            "customClass=\"$name\" customModule=\"$module\""
//        } else {
//            "customClass=\"$name\" customModuleProvider=\"target\""
//        }
//    }
//}
//
//data class XibDocument(
//    val owner: XibOwner? = null,
//    val view: XibView,
//    val resources: List<XibResource> = listOf()
//) {
//    override fun toString(): String {
//        return """
//            <?xml version="1.0" encoding="UTF-8"?>
//            <document type="com.apple.InterfaceBuilder3.CocoaTouch.XIB" version="3.0" toolsVersion="17156" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES">
//                <device id="retina3_5" orientation="portrait" appearance="light"/>
//                <dependencies>
//                    <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="17125"/>
//                    <capability name="Safe area layout guides" minToolsVersion="9.0"/>
//                    <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
//                </dependencies>
//                <objects>
//                    $owner
//                    <placeholder placeholderIdentifier="IBFirstResponder" id="-2" customClass="UIResponder"/>
//                    $view
//                </objects>
//                <resources>
//                ${resources.joinToString("\n")}
//                </resources>
//            </document>
//        """.trimIndent()
//    }
//}
//
//data class XibResource(
//    var type: String = "",
//    var name: String = "",
//    val attributes: Map<String, String> = mapOf()
//) {
//    override fun toString(): String {
//        return """<$type name="$name" ${attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }}/>"""
//    }
//}
//
//data class XibOwner(
//    var type: XibClassReference = XibClassReference(),
//    val outlets: Map<String, XibView> = mapOf()
//) {
//    override fun toString(): String {
//        return """
//            <placeholder placeholderIdentifier="IBFilesOwner" id="-1" userLabel="File's Owner" $type>
//                        <connections>
//                            ${
//            outlets.entries.joinToString {
//                """<outlet property="${it.key}" destination="${it.value.id}" id="${makeId()}"/>"""
//            }
//        }
//                        </connections>
//                    </placeholder>
//        """.trimIndent()
//    }
//}
//
//data class XibView(
//    var type: String = "",
//    var customClass: String? = null,
//    var customModule: String? = null,
//    val attributes: MutableMap<String, String> = mutableMapOf(),
//    val properties: MutableMap<String, XibNode> = mutableMapOf(),
//    val constraints: MutableList<XibConstraint> = mutableListOf(),
//    val userDefinedRuntimeAttributes: MutableMap<String, XibUDNode> = mutableMapOf(),
//    val subviews: MutableList<XibView> = mutableListOf(),
//    var id: String = makeId()
//) {
//    override fun toString(): String {
//        return buildString {
//            append("""<$type id="$id" translatesAutoresizingMaskIntoConstraints="NO" opaque="NO" """)
//            if (customClass != null) {
//                append("""customClass="$customClass" customModule="$customModule" """)
//            }
//            append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
//            appendln(">")
//            for (prop in properties) {
//                appendln(prop.value.toString(prop.key))
//            }
//            if (userDefinedRuntimeAttributes.isNotEmpty()) {
//                appendln("<userDefinedRuntimeAttributes>")
//                for (item in userDefinedRuntimeAttributes) {
//                    appendln(item.value.toString(item.key))
//                }
//                appendln("</userDefinedRuntimeAttributes>")
//            }
//            if (subviews.isNotEmpty()) {
//                appendln("<subviews>")
//                for (view in subviews) {
//                    appendln(view)
//                }
//                appendln("</subviews>")
//            }
//            if (constraints.isNotEmpty()) {
//                appendln("<constraints>")
//                for (c in constraints) {
//                    appendln(c)
//                }
//                appendln("</constraints>")
//            }
//            append("""</$type>""")
//        }
//    }
//}
//
//data class XibNode(
//    var nodeType: String = "",
//    val attributes: MutableMap<String, String> = mutableMapOf(),
//    val children: MutableList<XibNode> = mutableListOf()
//) {
//    override fun toString(): String = buildString {
//        append("""<$nodeType """)
//        append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
//        if (children.isEmpty()) {
//            appendln("/>")
//        } else {
//            appendln(">")
//            for (c in children) {
//                appendln(c.toString())
//            }
//            append("</$nodeType>")
//        }
//    }
//    fun toString(key: String): String = buildString {
//        append("""<$nodeType  key="$key" """)
//        append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
//        if (children.isEmpty()) {
//            appendln("/>")
//        } else {
//            appendln(">")
//            for (c in children) {
//                appendln(c.toString())
//            }
//            append("</$nodeType>")
//        }
//    }
//}
//
//
//data class XibUDNode(
//    var type: String = "",
//    val attributes: MutableMap<String, String> = mutableMapOf(),
//    val children: MutableList<XibNode> = mutableListOf()
//) {
//    override fun toString(): String = buildString {
//        append("""<userDefinedRuntimeAttribute type="$type" """)
//        append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
//        if (children.isEmpty()) {
//            appendln("/>")
//        } else {
//            appendln(">")
//            for (c in children) {
//                appendln(c.toString())
//            }
//            append("</userDefinedRuntimeAttribute>")
//        }
//    }
//    fun toString(keyPath: String): String = buildString {
//        append("""<userDefinedRuntimeAttribute type="$type" keyPath="$keyPath" """)
//        append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
//        if (children.isEmpty()) {
//            appendln("/>")
//        } else {
//            appendln(">")
//            for (c in children) {
//                appendln(c.toString())
//            }
//            append("</userDefinedRuntimeAttribute>")
//        }
//    }
//}
//
//
//data class XibConstraint(
//    var firstItem: String? = null,
//    var firstAttribute: String,
//    var secondItem: String,
//    var secondAttribute: String,
//    var constant: Double = 0.0,
//    var multiplier: Double = 1.0,
//    var priority: Int = 1000,
//    var id: String = makeId()
//) {
//    override fun toString(): String {
//        return buildString {
//            append("<constraint ")
//            if (firstItem != null) {
//                append("firstItem=\"$firstItem\" ")
//            }
//            append("firstAttribute=\"$firstAttribute\" ")
//            append("secondItem=\"$secondItem\" ")
//            append("secondAttribute=\"$secondAttribute\" ")
//            if (constant != 0.0) append("constant=\"$constant\" ")
//            if (multiplier != 1.0) append("multiplier=\"$multiplier\" ")
//            if (priority != 1000) append("priority\"$priority\" ")
//            append("id=\"$id\"/>")
//        }
//    }
//}
//
//data class XibNodeTemplate(
//    var nodeType: String = "",
//    var attributes: Map<String, Template> = mapOf(),
//    var children: List<XibNodeTemplate> = listOf()
//) {
//    fun resolve(templateResolver: (Template) -> String, into: XibNode = XibNode()): XibNode {
//        into.nodeType = nodeType
//        into.attributes.putAll(attributes.mapValues { templateResolver(it.value) })
//        into.children.addAll(children.map { it.resolve(templateResolver, XibNode()) })
//        return into
//    }
//}

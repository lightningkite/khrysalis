package com.lightningkite.khrysalis.swift.replacements.xib

import com.lightningkite.khrysalis.swift.replacements.Template


val availableLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
private fun idChar() = availableLetters.random()
fun makeId(): String {
    return "${idChar()}${idChar()}${idChar()}-${idChar()}${idChar()}-${idChar()}${idChar()}${idChar()}"
}

data class XibClassReference(
    val name: String = "",
    val module: String? = null
) {
    override fun toString(): String {
        return if(module != null){
            "customClass=\"$name\" customModule=\"$module\""
        } else {
            "customClass=\"$name\" customModuleProvider=\"target\""
        }
    }
}

data class XibDocument(
    val owner: XibOwner? = null,
    val view: XibView,
    val resources: List<XibResource> = listOf()
) {
    override fun toString(): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <document type="com.apple.InterfaceBuilder3.CocoaTouch.XIB" version="3.0" toolsVersion="17156" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES">
                <device id="retina3_5" orientation="portrait" appearance="light"/>
                <dependencies>
                    <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="17125"/>
                    <capability name="Safe area layout guides" minToolsVersion="9.0"/>
                    <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
                </dependencies>
                <objects>
                    $owner
                    <placeholder placeholderIdentifier="IBFirstResponder" id="-2" customClass="UIResponder"/>
                    $view
                </objects>
                <resources>
                ${resources.joinToString("\n")}
                </resources>
            </document>
        """.trimIndent()
    }
}

data class XibResource(
    var type: String = "",
    var name: String = "",
    val attributes: Map<String, String> = mapOf()
) {
    override fun toString(): String {
        return """<$type name="$name" ${attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }}/>"""
    }
}

data class XibOwner(
    var type: XibClassReference = XibClassReference(),
    val outlets: Map<String, XibView> = mapOf()
) {
    override fun toString(): String {
        return """
            <placeholder placeholderIdentifier="IBFilesOwner" id="-1" userLabel="File's Owner" $type>
                        <connections>
                            ${
            outlets.entries.joinToString {
                """<outlet property="${it.key}" destination="${it.value.id}" id="${makeId()}"/>"""
            }
        }
                        </connections>
                    </placeholder>
        """.trimIndent()
    }
}

data class XibView(
    var type: String = "",
    var customClass: String? = null,
    var customModule: String? = null,
    val attributes: MutableMap<String, String> = mutableMapOf(),
    val properties: MutableMap<String, XibNode> = mutableMapOf(),
    val constraints: MutableList<XibConstraint> = mutableListOf(),
    val userDefinedRuntimeAttributes: MutableMap<String, XibUDNode> = mutableMapOf(),
    val subviews: MutableList<XibView> = mutableListOf(),
    var id: String = makeId()
) {
    override fun toString(): String {
        return buildString {
            append("""<$type id="$id" translatesAutoresizingMaskIntoConstraints="NO" opaque="NO" """)
            if (customClass != null) {
                append("""customClass="$customClass" customModule="$customModule" """)
            }
            append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
            appendln(">")
            for (prop in properties) {
                appendln(prop.value.toString())
            }
            if (userDefinedRuntimeAttributes.isNotEmpty()) {
                appendln("<userDefinedRuntimeAttributes>")
                for (item in userDefinedRuntimeAttributes) {
                    appendln(item.toString())
                }
                appendln("</userDefinedRuntimeAttributes>")
            }
            if (subviews.isNotEmpty()) {
                appendln("<subviews>")
                for (view in subviews) {
                    appendln(view)
                }
                appendln("</subviews>")
            }
            if (constraints.isNotEmpty()) {
                appendln("<constraints>")
                for (c in constraints) {
                    appendln(c)
                }
                appendln("</constraints>")
            }
            append("""</$type>""")
        }
    }
}

data class XibNode(
    var nodeType: String = "",
    val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<XibNode> = mutableListOf()
) {
    override fun toString(): String = buildString {
        append("""<$nodeType """)
        append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
        if (children.isEmpty()) {
            appendln("/>")
        } else {
            appendln(">")
            for (c in children) {
                appendln(c.toString())
            }
            append("</$nodeType>")
        }
    }
}


data class XibUDNode(
    var type: String = "",
    var keyPath: String = "",
    val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<XibNode> = mutableListOf()
) {
    override fun toString(): String = buildString {
        append("""<userDefinedRuntimeAttribute type="$type" keyPath="$keyPath" """)
        append(attributes.entries.joinToString(" ") { it.key + "=\"" + it.value + "\"" })
        if (children.isEmpty()) {
            appendln("/>")
        } else {
            appendln(">")
            for (c in children) {
                appendln(c.toString())
            }
            append("</userDefinedRuntimeAttribute>")
        }
    }
}


data class XibConstraint(
    var firstItem: String? = null,
    var firstAttribute: String,
    var secondItem: String,
    var secondAttribute: String,
    var constant: Double = 0.0,
    var multiplier: Double = 1.0,
    var priority: Int = 1000,
    var id: String = makeId()
) {
    override fun toString(): String {
        return buildString {
            append("<constraint ")
            if (firstItem != null) {
                append("firstItem=\"$firstItem\" ")
            }
            append("firstAttribute=\"$firstAttribute\" ")
            append("secondItem=\"$secondItem\" ")
            append("secondAttribute=\"$secondAttribute\" ")
            if (constant != 0.0) append("constant=\"$constant\" ")
            if (multiplier != 1.0) append("multiplier=\"$multiplier\" ")
            if (priority != 1000) append("priority\"$priority\" ")
            append("id=\"$id\"/>")
        }
    }
}

data class XibNodeTemplate(
    var nodeType: String = "",
    var attributes: Map<String, Template> = mapOf(),
    var children: List<XibNodeTemplate> = listOf()
) {
    fun resolve(templateResolver: (Template) -> String, into: XibNode = XibNode()): XibNode {
        into.nodeType = nodeType
        into.attributes.putAll(attributes.mapValues { templateResolver(it.value) })
        into.children.addAll(children.map { it.resolve(templateResolver, XibNode()) })
        return into
    }
}


data class XibUDNodeTemplate(
    var type: String = "",
    var keyPath: String = "",
    var attributes: Map<String, Template> = mapOf(),
    var children: List<XibNodeTemplate> = listOf()
) {
    fun resolve(templateResolver: (Template) -> String, into: XibUDNode = XibUDNode()): XibUDNode {
        into.type = type
        into.keyPath = keyPath
        into.attributes.putAll(attributes.mapValues { templateResolver(it.value) })
        into.children.addAll(children.map { it.resolve(templateResolver, XibNode()) })
        return into
    }
}


data class XibConstraintTemplate(
    var firstItem: Template? = null,
    var firstAttribute: String,
    var secondItem: Template,
    var secondAttribute: String,
    var constant: Template? = null,
    var multiplier: Template? = null,
    var priority: Template? = null
) {
    fun resolve(templateResolver: (Template) -> String): XibConstraint {
        return XibConstraint(
            firstItem = firstItem?.let(templateResolver),
            firstAttribute = firstAttribute,
            secondItem = templateResolver(secondItem),
            secondAttribute = secondAttribute,
            constant = constant?.let(templateResolver)?.toDoubleOrNull() ?: 0.0,
            multiplier = multiplier?.let(templateResolver)?.toDoubleOrNull() ?: 1.0,
            priority = priority?.let(templateResolver)?.toIntOrNull() ?: 1000
        )
    }
}
package com.lightningkite.khrysalis.swift.replacements.xib

import com.fasterxml.jackson.annotation.JsonProperty


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
        children.find { it.attributes[key] == value }?.let { return it }
        val newNode = PureXmlOut(name)
        newNode.attributes[key] = value
        children.add(newNode)
        return newNode
    }

    override fun toString(): String = buildString { write(this) }
    fun write(to: Appendable, tab: Int = 0): Unit = with(to) {
        append("    ".repeat(tab))
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
            appendln("/>")
        } else {
            appendln('>')
            for (child in children) {
                child.write(to, tab + 1)
            }
            append("    ".repeat(tab))
            append("</")
            append(name)
            appendln('>')
        }
    }
}

data class XibTranslation(
    val name: String,
    val deferTo: String? = null,
    val defaults: Map<AttPath, XibTypedValue> = mapOf(),
    val attributes: Map<String, AttHandler> = mapOf()
)

data class XibTypedValue(
    val value: String,
    val type: AttKind = AttKind.Raw
) {

}

data class AttPath(
    val pathType: AttPathType,
    val name: String,
    val type: String = name,
    val then: AttPath? = null
) {
    constructor(string: String) : this(
        pathType = AttPathType.values().find { it.name.equals(string.substringBefore('/'), true) }!!,
        name = string.substringAfter('/', "").substringBefore('/').substringBefore(':'),
        type = string.substringAfter('/', "").substringBefore('/').substringAfter(':').substringBefore('/'),
        then = string.substringAfter('/', "").substringAfter('/', "").takeUnless { it.isEmpty() }
            ?.let { AttPath(it) }
    )

    override fun toString(): String {
        return "$pathType/$name:$type" + if(then != null) "/$then" else ""
    }
}

data class AttHandler(
    val path: AttPath? = null,
    @JsonProperty("as") val asKind: AttKind? = null,
    val constant: String? = null,

    val mapValues: Map<String, Map<AttPath, XibTypedValue>>? = null,

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
    Font, Dimension, Color, Number, Raw, Text, Bool, Image, Drawable;
    companion object {
        fun parse(string: String): AttKind {
            return when(string.toLowerCase()){
                "font" -> AttKind.Font
                "dimension", "dim" -> AttKind.Dimension
                "color" -> AttKind.Color
                "number", "int", "num" -> AttKind.Number
                "raw", "direct" -> AttKind.Raw
                "text", "string" -> AttKind.Text
                "boolean", "bool" -> AttKind.Bool
                "image" -> AttKind.Image
                "drawable" -> AttKind.Drawable
                else -> throw IllegalArgumentException("Unknown type $string")
            }
        }
    }
}

enum class AttHandlerKind {
    Direct, Multiple, ValueMap
}

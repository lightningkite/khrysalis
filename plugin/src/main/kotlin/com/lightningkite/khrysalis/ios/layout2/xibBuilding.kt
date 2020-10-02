package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.ios.layout2.models.IosColor
import com.lightningkite.khrysalis.swift.replacements.Replacements
import com.lightningkite.khrysalis.swift.replacements.TypeReplacement
import com.lightningkite.khrysalis.swift.replacements.xib.*
import com.lightningkite.khrysalis.utils.XmlNode
import java.io.File

typealias CodeRule = (CanResolveValue, XmlNode, PureXmlOut) -> Unit

data class IosFont(val family: String, val name: String, val file: File? = null)
interface CanResolveValue {
    fun resolveFont(string: String): IosFont?
    fun resolveDimension(string: String): String
    fun resolveColor(string: String): Any //IosColor or String
    fun resolveString(string: String): String
    fun resolveImage(string: String): String?
    fun resolveDrawable(string: String): String?
}

fun PureXmlOut.addStandardViewProperties() {
    attributes["translatesAutoresizingMaskIntoConstraints"] = "NO"
    attributes["opaque"] = "NO"
}

fun Replacements.typeReplacementsForName(name: String): List<TypeReplacement> =
    generateSequence(typeReplacementForName(name)) {
        it.xib?.deferTo?.let { this.types[it]?.last() }
    }.toList()

fun Replacements.typeReplacementForName(name: String): TypeReplacement? {
    return this.types["android.view.${name}"]?.last()
        ?: this.types["android.widget.${name}"]?.last()
        ?: this.types[name]?.last()
}

fun Replacements.translate(resolver: CanResolveValue, node: XmlNode): PureXmlOut {
    val rules = typeReplacementsForName(node.name)
    if (rules.isEmpty()) return PureXmlOut("view").apply { addStandardViewProperties(); attributes["id"] = makeId() }
    val out = PureXmlOut(rules.first().xib!!.name)
    out.attributes["id"] = node.tags["id"]!!
    out.addStandardViewProperties()
    rules.asSequence()
        .flatMap { it.xib?.defaults?.entries?.asSequence() ?: sequenceOf() }
        .forEach {
            try {
                it.key.resolve(out).put(it.value.type, it.value.value, resolver)
            } catch(e: Exception){
                throw Exception("Error while processing default (${it.key} = ${it.value}) for ${node.name}:", e)
            }
        }
    for (a in node.parts) {
        val matching = rules.asSequence().map { it.xib?.attributes?.get(a.type) }.firstOrNull()
        try {
            matching?.invoke(resolver, a, out)
        } catch(e: Exception){
            throw Exception("Error while processing rule ${a.type}:", e)
        }
    }
    for (child in node.children) {
        out.getOrPutChild("subviews").children.add(translate(resolver, child))
    }
    for (r in rules) {
        extraProcessingRules[r.id]?.invoke(resolver, node, out)
    }
    return out
}

sealed class AttPathDestination {
    open val out: PureXmlOut? get() = null
    abstract fun put(kind: AttKind, value: String, resolver: CanResolveValue)

    data class Property(override val out: PureXmlOut) : AttPathDestination() {
        override fun put(kind: AttKind, value: String, resolver: CanResolveValue) {
            when (kind) {
                AttKind.Font -> {
                    val font = resolver.resolveFont(value)
                    font?.name?.let { out.attributes["name"] = it }
                    font?.family?.let { out.attributes["family"] = it }
                }
                AttKind.Dimension -> with(out) {
                    name = "real"
                    attributes["key"] = "value"
                    attributes["value"] = resolver.resolveDimension(value)
                }
                AttKind.Color -> {
                    out.name = "color"
                    when (val item = resolver.resolveColor(value)) {
                        is String -> {
                            out.attributes["name"] = item
                        }
                        is IosColor -> {
                            out.attributes["red"] = item.red.toString()
                            out.attributes["green"] = item.green.toString()
                            out.attributes["blue"] = item.blue.toString()
                            out.attributes["alpha"] = item.alpha.toString()
                            out.attributes["colorSpace"] = "custom"
                            out.attributes["customColorSpace"] = "sRGB"
                        }
                    }
                }
                AttKind.Number -> with(out) {
                    name = "real"
                    attributes["key"] = "value"
                    attributes["value"] = value
                }
                AttKind.Raw -> with(out) {
                    name = "string"
                    attributes["key"] = "value"
                    attributes["value"] = value
                }
                AttKind.Text -> with(out) {
                    name = "string"
                    attributes["key"] = "value"
                    attributes["value"] = resolver.resolveString(value)
                }
                AttKind.Bool -> with(out) {
                    name = "boolean"
                    attributes["key"] = "value"
                    attributes["value"] = if (value == "true") "YES" else "NO"
                }
                else -> throw IllegalStateException("Cannot be applied to this position. kind: $kind")
            }
        }
    }

    data class UserDefined(override val out: PureXmlOut) : AttPathDestination() {
        override fun put(kind: AttKind, value: String, resolver: CanResolveValue) {
            when (kind) {
                AttKind.Dimension -> {
                    out.attributes["type"] = "number"
                    out.children.add(PureXmlOut("real").apply {
                        attributes["key"] = "value"
                        attributes["value"] = resolver.resolveDimension(value)
                    })
                }
                AttKind.Number -> {
                    out.attributes["type"] = "number"
                    out.children.add(PureXmlOut("real").apply {
                        attributes["key"] = "value"
                        attributes["value"] = value
                    })
                }
                AttKind.Color -> {
                    out.attributes["type"] = "color"
                    out.children.add(PureXmlOut("color").apply {
                        attributes["key"] = "value"
                        when (val item = resolver.resolveColor(value)) {
                            is String -> {
                                attributes["name"] = item
                            }
                            is IosColor -> {
                                attributes["red"] = item.red.toString()
                                attributes["green"] = item.green.toString()
                                attributes["blue"] = item.blue.toString()
                                attributes["alpha"] = item.alpha.toString()
                                attributes["colorSpace"] = "custom"
                                attributes["customColorSpace"] = "sRGB"
                            }
                        }
                    })
                }
                AttKind.Raw -> {
                    out.attributes["type"] = "string"
                    out.attributes["value"] = value
                }
                AttKind.Text -> {
                    out.attributes["type"] = "string"
                    out.attributes["value"] = resolver.resolveString(value)
                }
                AttKind.Bool -> {
                    out.attributes["type"] = "boolean"
                    out.attributes["value"] = if (value == "true") "YES" else "NO"
                }
                else -> throw IllegalStateException("Cannot be applied to this position. kind: $kind")
            }
        }
    }

    data class Attribute(val set: (String) -> Unit) : AttPathDestination() {
        override fun put(kind: AttKind, value: String, resolver: CanResolveValue) {
            when (kind) {
                AttKind.Dimension -> set(resolver.resolveDimension(value))
                AttKind.Number -> set(value)
                AttKind.Raw -> if (value == "<id>") set(makeId()) else set(value)
                AttKind.Text -> set(resolver.resolveString(value))
                AttKind.Font -> resolver.resolveFont(value)?.name?.let { set(it) }
                AttKind.Bool -> if (value == "true") set("YES") else set("NO")
                AttKind.Image -> resolver.resolveImage(value)?.let { set(it) }
                AttKind.Drawable -> resolver.resolveDrawable(value)?.let { set(it) }
                else -> throw IllegalStateException("Cannot be applied to this position. kind: $kind")
            }
        }
    }
}

fun AttPath.resolve(from: PureXmlOut): AttPathDestination {
    var currentPath = this
    var currentNode: PureXmlOut = from
    while (true) {
        val dest: AttPathDestination = when (currentPath.pathType) {
            AttPathType.Property -> AttPathDestination.Property(
                currentNode.getOrPutChildKeyed(
                    "key",
                    currentPath.name,
                    this.type
                )
            )
            AttPathType.UserDefined -> AttPathDestination.UserDefined(
                currentNode.getOrPutChild("userDefinedRuntimeAttributes")
                    .getOrPutChildKeyed("keyPath", currentPath.name, "userDefinedRuntimeAttribute")
            )
            AttPathType.Attribute -> AttPathDestination.Attribute { currentNode.attributes[currentPath.name] = it }
        }
        if (currentPath.then == null) return dest
        else {
            currentPath = currentPath.then!!
            currentNode = dest.out ?: throw java.lang.IllegalStateException("Attribute cannot have children")
        }
    }
}
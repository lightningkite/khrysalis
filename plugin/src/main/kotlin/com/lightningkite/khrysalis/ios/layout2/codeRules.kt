package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.ios.layout.horizontalGravityWords
import com.lightningkite.khrysalis.ios.layout.verticalGravityWords
import com.lightningkite.khrysalis.swift.replacements.Replacements
import com.lightningkite.khrysalis.swift.replacements.xib.AttKind
import com.lightningkite.khrysalis.swift.replacements.xib.AttPath
import com.lightningkite.khrysalis.swift.replacements.xib.PureXmlOut
import com.lightningkite.khrysalis.swift.replacements.xib.makeId
import com.lightningkite.khrysalis.utils.XmlNode

fun PureXmlOut.constrain(
    firstItem: String,
    firstAttribute: String,
    secondItem: String,
    secondAttribute: String,
    relation: String? = null,
    constant: String? = null,
    multiplier: String? = null,
    priority: String? = null
) {
    this.getOrPutChild("constraints").children.add(PureXmlOut("constraint").apply {
        attributes["id"] = makeId()
        attributes["firstItem"] = firstItem
        attributes["firstAttribute"] = firstAttribute
        attributes["secondItem"] = secondItem
        attributes["secondAttribute"] = secondAttribute
        priority?.let { attributes["priority"] = it }
        constant?.let { attributes["constant"] = it }
        multiplier?.let { attributes["multiplier"] = it }
        relation?.let { attributes["relation"] = it }
    })
}

fun PureXmlOut.constrainSelf(
    attribute: String,
    relation: String? = null,
    constant: String? = null
) {
    this.getOrPutChild("constraints").children.add(PureXmlOut("constraint").apply {
        attributes["id"] = makeId()
        attributes["firstItem"] = this.attributes["id"]!!
        attributes["firstAttribute"] = attribute
        constant?.let { attributes["constant"] = it }
        relation?.let { attributes["relation"] = it }
    })
}

fun Replacements.paddingUnhandled(type: String): Boolean {
    return typeReplacementsForName(type)
        .any { it.xib?.attributes?.get("android:padding") != null }
}

fun XmlNode.getLeftMargin(resolver: CanResolveValue, rules: Replacements): String {
    val directMargin = resolver.resolveDimension(
        allAttributes["android:layout_marginStart"]
            ?: allAttributes["android:layout_marginLeft"]
            ?: allAttributes["android:layout_margin"] ?: "0dp"
    )
    return if(rules.paddingUnhandled(name)){
        val padding = resolver.resolveDimension(
            allAttributes["android:paddingStart"]
                ?: allAttributes["android:paddingLeft"]
                ?: allAttributes["android:padding"] ?: "0dp"
        )
        (directMargin.toDouble() + padding.toDouble()).toString()
    } else directMargin
}

fun XmlNode.getTopMargin(resolver: CanResolveValue, rules: Replacements): String {
    val directMargin = resolver.resolveDimension(
        allAttributes["android:layout_marginTop"]
            ?: allAttributes["android:layout_margin"] ?: "0dp"
    )
    return if(rules.paddingUnhandled(name)){
        val padding = resolver.resolveDimension(
            allAttributes["android:paddingTop"]
                ?: allAttributes["android:padding"] ?: "0dp"
        )
        (directMargin.toDouble() + padding.toDouble()).toString()
    } else directMargin
}

fun XmlNode.getRightMargin(resolver: CanResolveValue, rules: Replacements): String {
    val directMargin = resolver.resolveDimension(
        allAttributes["android:layout_marginEnd"]
            ?: allAttributes["android:layout_marginRight"]
            ?: allAttributes["android:layout_margin"] ?: "0dp"
    )
    return if(rules.paddingUnhandled(name)){
        val padding = resolver.resolveDimension(
            allAttributes["android:paddingEnd"]
                ?: allAttributes["android:paddingRight"]
                ?: allAttributes["android:padding"] ?: "0dp"
        )
        (directMargin.toDouble() + padding.toDouble()).toString()
    } else directMargin
}

fun XmlNode.getBottomMargin(resolver: CanResolveValue, rules: Replacements): String {
    val directMargin = resolver.resolveDimension(
        allAttributes["android:layout_marginBottom"]
            ?: allAttributes["android:layout_margin"] ?: "0dp"
    )
    return if(rules.paddingUnhandled(name)){
        val padding = resolver.resolveDimension(
            allAttributes["android:paddingBottom"]
                ?: allAttributes["android:padding"] ?: "0dp"
        )
        (directMargin.toDouble() + padding.toDouble()).toString()
    } else directMargin
}

fun PureXmlOut.frameChildHorizontal(
    myNode: XmlNode,
    childNode: XmlNode,
    resolver: CanResolveValue,
    rules: Replacements
) {
    val gravity =
        (childNode.allAttributes["android:layout_gravity"] ?: myNode.allAttributes["android:gravity"])?.split('|')
            ?.find { it in horizontalGravityWords.keys }
            ?: "start"
    var startExact = false
    var endExact = false
    var useCenter = false
    when (gravity) {
        "start", "left" -> startExact = true
        "end", "right" -> endExact = true
        "center_horizontal", "center" -> useCenter = true
    }
    if (childNode.allAttributes["android:layout_width"] == "match_parent") {
        startExact = true
        endExact = true
        useCenter = false
    }
    constrain(
        firstItem = childNode.tags["id"]!!,
        firstAttribute = "leading",
        secondItem = this.attributes["id"]!!,
        secondAttribute = "leadingMargin",
        relation = if (startExact) null else "greaterThanOrEqual",
        constant = childNode.getLeftMargin(resolver, rules)
    )
    constrain(
        secondItem = childNode.tags["id"]!!,
        secondAttribute = "trailing",
        firstItem = this.attributes["id"]!!,
        firstAttribute = "trailingMargin",
        relation = if (endExact) null else "greaterThanOrEqual",
        constant = childNode.getRightMargin(resolver, rules)
    )
    if (useCenter) {
        constrain(
            firstItem = childNode.tags["id"]!!,
            firstAttribute = "centerX",
            secondItem = this.attributes["id"]!!,
            secondAttribute = "centerX"
        )
    }
}

fun PureXmlOut.frameChildVertical(
    myNode: XmlNode,
    childNode: XmlNode,
    resolver: CanResolveValue,
    rules: Replacements
) {
    val gravity =
        (childNode.allAttributes["android:layout_gravity"] ?: myNode.allAttributes["android:gravity"])?.split('|')
            ?.find { it in verticalGravityWords.keys } ?: "top"
    var startExact = false
    var endExact = false
    var useCenter = false
    when (gravity) {
        "top" -> startExact = true
        "bottom" -> endExact = true
        "center_vertical", "center" -> useCenter = true
    }
    if (childNode.allAttributes["android:layout_height"] == "match_parent") {
        startExact = true
        endExact = true
        useCenter = false
    }
    constrain(
        firstItem = childNode.tags["id"]!!,
        firstAttribute = "top",
        secondItem = this.attributes["id"]!!,
        secondAttribute = "topMargin",
        relation = if (startExact) null else "greaterThanOrEqual",
        constant = childNode.getTopMargin(resolver, rules)
    )
    constrain(
        secondItem = childNode.tags["id"]!!,
        secondAttribute = "bottom",
        firstItem = this.attributes["id"]!!,
        firstAttribute = "bottomMargin",
        relation = if (endExact) null else "greaterThanOrEqual",
        constant = childNode.getBottomMargin(resolver, rules)
    )
    if (useCenter) {
        constrain(
            firstItem = childNode.tags["id"]!!,
            firstAttribute = "centerY",
            secondItem = this.attributes["id"]!!,
            secondAttribute = "centerY"
        )
    }
}

fun PureXmlOut.handleSize(
    myNode: XmlNode,
    resolver: CanResolveValue
) {
    var skipHorizontal = false
    var skipVertical = false
    if (myNode.parent?.name == "LinearLayout" && myNode.allAttributes["android:layout_weight"] != null) {
        val vertical = myNode.parent?.allAttributes?.get("android:orientation") == "vertical"
        if (vertical) skipVertical = true else skipHorizontal = true
    }
    if (!skipHorizontal) {
        when (val it = myNode.allAttributes["android:layout_width"]) {
            "match_parent" -> {
            } //constraints already added by frameChildX
            "wrap_content", null -> {
            } //no constraints needed
            else -> constrainSelf("width", constant = resolver.resolveDimension(it))
        }
    }
    if (!skipVertical) {
        when (val it = myNode.allAttributes["android:layout_height"]) {
            "match_parent" -> {
            } //constraints already added by frameChildX
            "wrap_content", null -> {
            } //no constraints needed
            else -> constrainSelf("height", constant = resolver.resolveDimension(it))
        }
    }
    myNode.allAttributes["android:minWidth"]?.let {
        constrainSelf("width", constant = resolver.resolveDimension(it))
    }
    myNode.allAttributes["android:minHeight"]?.let {
        constrainSelf("height", constant = resolver.resolveDimension(it))
    }
}

val extraProcessingRules: Map<String, CodeRule> = mapOf(
    "android.widget.ScrollView" to label@{ replacements, resolver, node, out ->
        val child = node.children.firstOrNull() ?: return@label
        out.constrain(
            firstItem = child.tags["id"]!!,
            firstAttribute = "leading",
            secondItem = out.attributes["id"]!!,
            secondAttribute = "leadingMargin",
            priority = "900"
        )
        out.constrain(
            firstItem = child.tags["id"]!!,
            firstAttribute = "trailing",
            secondItem = out.attributes["id"]!!,
            secondAttribute = "trailingMargin",
            priority = "900"
        )
    },
    "android.widget.HorizontalScrollView" to label@{ replacements, resolver, node, out ->
        val child = node.children.firstOrNull() ?: return@label
        out.constrain(
            firstItem = child.tags["id"]!!,
            firstAttribute = "top",
            secondItem = out.attributes["id"]!!,
            secondAttribute = "topMargin",
            priority = "900"
        )
        out.constrain(
            firstItem = child.tags["id"]!!,
            firstAttribute = "bottom",
            secondItem = out.attributes["id"]!!,
            secondAttribute = "bottomMargin",
            priority = "900"
        )
    },
    "android.widget.LinearLayout" to { replacements, resolver, node, out ->
        val gravityAtt = node.allAttributes["android:gravity"] ?: ""
        if (node.allAttributes["android:orientation"] == "vertical") {
            for (child in node.children) {
                out.frameChildHorizontal(node, child, resolver, replacements)
            }
            val alignmentString = when (gravityAtt.split('|').find { it in horizontalGravityWords }) {
                "center", "center_vertical" -> "center"
                "top" -> "start"
                "bottom" -> "end"
                else -> "start"
            }
            AttPath("userDefined/alignmentString").resolve(out).put(AttKind.Raw, alignmentString, resolver)
        } else {
            for (child in node.children) {
                out.frameChildVertical(node, child, resolver, replacements)
            }
            val alignmentString = when (gravityAtt.split('|').find { it in horizontalGravityWords }) {
                "center", "center_horizontal" -> "center"
                "start", "left" -> "start"
                "end", "right" -> "end"
                else -> "start"
            }
            AttPath("userDefined/alignmentString").resolve(out).put(AttKind.Raw, alignmentString, resolver)
        }

        val weightSizeAttr = if (node.allAttributes["android:orientation"] == "vertical") "height" else "width"
        val firstWithWeight = node.children.find { it.allAttributes["android:layout_weight"] != null }
        if (firstWithWeight != null) {
            val firstWeight = firstWithWeight.allAttributes["android:layout_weight"]!!.toDouble()

            for (child in node.children) {
                if (child == firstWithWeight) continue
                val weight = child.allAttributes["android:layout_weight"]?.toDouble() ?: continue
                val ratio = weight / firstWeight
                out.constrain(
                    firstItem = child.tags["id"]!!,
                    firstAttribute = weightSizeAttr,
                    secondItem = firstWithWeight.tags["id"]!!,
                    secondAttribute = weightSizeAttr,
                    multiplier = ratio.toString(),
                    priority = "500"
                )
            }
        }

//        val startAttr = if (node.allAttributes["android:orientation"] == "vertical") "top" else "leading"
//        val endAttr = if (node.allAttributes["android:orientation"] == "vertical") "bottom" else "trailing"
//
//        var prevAttr = startAttr + "Margin"
//        var prevView = out.attributes["id"]!!
//        for (child in node.children) {
//            out.constrain(
//                firstItem = child.tags["id"]!!,
//                firstAttribute = startAttr,
//                secondItem = prevView,
//                secondAttribute = prevAttr,
//                priority = "2"
//            )
//            prevView = child.tags["id"]!!
//            prevAttr = endAttr
//        }
//        out.constrain(
//            firstItem = prevView,
//            firstAttribute = prevAttr,
//            secondItem = out.attributes["id"]!!,
//            secondAttribute = endAttr + "Margin",
//            priority = "2"
//        )
    },
    "android.widget.FrameLayout" to { replacements, resolver, node, out ->
        for (child in node.children) {
            out.frameChildHorizontal(node, child, resolver, replacements)
            out.frameChildVertical(node, child, resolver, replacements)
        }
    },
    "android.view.View" to { replacements, resolver, node, out ->
        out.handleSize(node, resolver)

        val backgroundValue = node.allAttributes["android:background"]
        when {
            backgroundValue == null -> {
            }
            backgroundValue.startsWith("@draw") -> {
                AttPath("userDefined/backgroundDrawableResource").resolve(out)
                    .put(AttKind.Raw, backgroundValue.substringAfter('/'), resolver)
            }
            backgroundValue.startsWith("@col") -> {
                AttPath("property/backgroundColor:color").resolve(out).put(AttKind.Color, backgroundValue, resolver)
            }
            backgroundValue.startsWith("#") -> {
                AttPath("property/backgroundColor:color").resolve(out).put(AttKind.Color, backgroundValue, resolver)
            }
        }

        node.parent?.takeIf { it.name == "LinearLayout" }?.let { lin ->
            if (lin.allAttributes["android:orientation"] == "vertical") {
                if (node.allAttributes["android:layout_weight"] != null) {
                    out.attributes["verticalHuggingPriority"] = "5"
                }
                AttPath("userDefined/ssv_start").resolve(out).put(AttKind.Dimension, node.getTopMargin(resolver, replacements), resolver)
                AttPath("userDefined/ssv_end").resolve(out).put(AttKind.Dimension, node.getBottomMargin(resolver, replacements), resolver)
            } else {
                if (node.allAttributes["android:layout_weight"] != null) {
                    out.attributes["horizontalHuggingPriority"] = "5"
                }
                AttPath("userDefined/ssv_start").resolve(out).put(AttKind.Dimension, node.getLeftMargin(resolver, replacements), resolver)
                AttPath("userDefined/ssv_end").resolve(out).put(AttKind.Dimension, node.getRightMargin(resolver, replacements), resolver)
            }
        }
    }
)
package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.ios.layout.horizontalGravityWords
import com.lightningkite.khrysalis.ios.layout.verticalGravityWords
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

fun PureXmlOut.frameChildHorizontal(
    childNode: XmlNode,
    resolver: CanResolveValue
) {
    val gravity =
        childNode.allAttributes["android:layout_gravity"]?.split('|')?.find { it in horizontalGravityWords.keys }
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
        constant = resolver.resolveDimension(
            childNode.allAttributes["android:layout_marginStart"]
                ?: childNode.allAttributes["android:layout_marginLeft"]
                ?: childNode.allAttributes["android:layout_margin"] ?: "0dp"
        )
    )
    constrain(
        secondItem = childNode.tags["id"]!!,
        secondAttribute = "trailing",
        firstItem = this.attributes["id"]!!,
        firstAttribute = "trailingMargin",
        relation = if (endExact) null else "greaterThanOrEqual",
        constant = resolver.resolveDimension(
            childNode.allAttributes["android:layout_marginEnd"] ?: childNode.allAttributes["android:layout_marginRight"]
            ?: childNode.allAttributes["android:layout_margin"] ?: "0dp"
        )
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
    childNode: XmlNode,
    resolver: CanResolveValue
) {
    val gravity =
        childNode.allAttributes["android:layout_gravity"]?.split('|')?.find { it in verticalGravityWords.keys } ?: "top"
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
        constant = resolver.resolveDimension(
            childNode.allAttributes["android:layout_marginTop"] ?: childNode.allAttributes["android:layout_margin"]
            ?: "0dp"
        )
    )
    constrain(
        secondItem = childNode.tags["id"]!!,
        secondAttribute = "bottom",
        firstItem = this.attributes["id"]!!,
        firstAttribute = "bottomMargin",
        relation = if (endExact) null else "greaterThanOrEqual",
        constant = resolver.resolveDimension(
            childNode.allAttributes["android:layout_marginBottom"] ?: childNode.allAttributes["android:layout_margin"]
            ?: "0dp"
        )
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
    when (val it = myNode.allAttributes["android:layout_width"]) {
        "match_parent" -> {
        } //constraints already added by frameChildX
        "wrap_content", null -> {
        } //no constraints needed
        else -> constrainSelf("width", constant = resolver.resolveDimension(it))
    }
    when (val it = myNode.allAttributes["android:layout_height"]) {
        "match_parent" -> {
        } //constraints already added by frameChildX
        "wrap_content", null -> {
        } //no constraints needed
        else -> constrainSelf("height", constant = resolver.resolveDimension(it))
    }
    myNode.allAttributes["android:minWidth"]?.let {
        constrainSelf("width", constant = resolver.resolveDimension(it))
    }
    myNode.allAttributes["android:minHeight"]?.let {
        constrainSelf("height", constant = resolver.resolveDimension(it))
    }
}

val extraProcessingRules: Map<String, CodeRule> = mapOf(
    "android.widget.LinearLayout" to { resolver, node, out ->
        //TODO: Default gravity
        if (node.allAttributes["android:orientation"] == "vertical") {
            for (child in node.children) {
                out.frameChildHorizontal(child, resolver)
            }
        } else {
            for (child in node.children) {
                out.frameChildVertical(child, resolver)
            }
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
    },
    "android.widget.FrameLayout" to { resolver, node, out ->
        for (child in node.children) {
            out.frameChildHorizontal(child, resolver)
            out.frameChildVertical(child, resolver)
        }
    },
    "android.view.View" to { resolver, node, out ->
        out.handleSize(node, resolver)

        val backgroundValue = node.allAttributes["android:background"]
        when {
            backgroundValue == null -> {}
            backgroundValue.startsWith("@draw") -> {
                AttPath("userDefined/backgroundDrawableResource").resolve(out).put(AttKind.Raw, backgroundValue.substringAfter('/'), resolver)
            }
            backgroundValue.startsWith("@col") -> {
                AttPath("property/backgroundColor:color").resolve(out).put(AttKind.Color, backgroundValue, resolver)
            }
            backgroundValue.startsWith("#") -> {
                AttPath("property/backgroundColor:color").resolve(out).put(AttKind.Color, backgroundValue, resolver)
            }
        }
        
        node.parent?.takeIf { it.name == "LinearLayout" }?.let { lin ->
            if(lin.allAttributes["android:orientation"] == "vertical"){
                if(node.allAttributes["android:layout_weight"] != null){
                    out.attributes["verticalHuggingPriority"] = "5"
                }
                val startMargin = resolver.resolveDimension(
                    node.allAttributes["android:layout_marginTop"] ?: node.allAttributes["android:layout_margin"]
                    ?: "0dp"
                )
                val endMargin = resolver.resolveDimension(
                    node.allAttributes["android:layout_marginBottom"] ?: node.allAttributes["android:layout_margin"]
                    ?: "0dp"
                )
                AttPath("userDefined/ssv_start").resolve(out).put(AttKind.Dimension, startMargin, resolver)
                AttPath("userDefined/ssv_end").resolve(out).put(AttKind.Dimension, endMargin, resolver)
            } else {
                if(node.allAttributes["android:layout_weight"] != null){
                    out.attributes["horizontalHuggingPriority"] = "5"
                }
                val startMargin = resolver.resolveDimension(
                    node.allAttributes["android:layout_marginStart"]
                        ?: node.allAttributes["android:layout_marginLeft"]
                        ?: node.allAttributes["android:layout_margin"] ?: "0dp"
                )
                val endMargin = resolver.resolveDimension(
                    node.allAttributes["android:layout_marginEnd"] ?: node.allAttributes["android:layout_marginRight"]
                    ?: node.allAttributes["android:layout_margin"] ?: "0dp"
                )
                AttPath("userDefined/ssv_start").resolve(out).put(AttKind.Dimension, startMargin, resolver)
                AttPath("userDefined/ssv_end").resolve(out).put(AttKind.Dimension, endMargin, resolver)
            }
        }
    }
)
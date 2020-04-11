package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.asCssDimension
import com.lightningkite.khrysalis.web.attributeAsCssDimension


internal fun HtmlTranslator.layout() {

    fun handleCommonLayoutStuff(rule: XmlNode, child: ResultNode, container: ResultNode) {
        rule.allAttributes["android:layout_width"]?.let { value ->
            when (value) {
                "match_parent" -> {
                    child.style["width"] = "100%"
                    container.style["width"] = "100%"
                }
                "wrap_content" -> {
                }
                else -> {
                    val converted = value.asCssDimension() ?: "failedConversion"
                    child.style["width"] = converted
                    container.style["width"] = converted
                }
            }
        }
        rule.allAttributes["android:layout_height"]?.let { value ->
            when (value) {
                "match_parent" -> {
                    child.style["height"] = "100%"
                    container.style["height"] = "100%"
                }
                "wrap_content" -> {
                }
                else -> {
                    val converted = value.asCssDimension() ?: "failedConversion"
                    child.style["height"] = converted
                    container.style["height"] = converted
                }
            }
        }
        rule.attributeAsCssDimension("android:layout_margin")?.let { value ->
            container.style["padding"] = value
        }
        rule.attributeAsCssDimension("android:layout_marginLeft")?.let { value ->
            container.style["padding-left"] = value
        }
        rule.attributeAsCssDimension("android:layout_marginRight")?.let { value ->
            container.style["padding-right"] = value
        }
        rule.attributeAsCssDimension("android:layout_marginStart")?.let { value ->
            container.style["padding-left"] = value
        }
        rule.attributeAsCssDimension("android:layout_marginEnd")?.let { value ->
            container.style["padding-right"] = value
        }
        rule.attributeAsCssDimension("android:layout_marginTop")?.let { value ->
            container.style["padding-top"] = value
        }
        rule.attributeAsCssDimension("android:layout_marginBottom")?.let { value ->
            container.style["padding-bottom"] = value
        }
        rule.allAttributes["android:layout_weight"]?.let { value ->
            container.style["flex-grow"] = value
        }

    }
    element.handle("LinearLayout") {
        out.style["display"] = "flex"
        val isVertical = rule.allAttributes["android:orientation"] == "vertical"
        out.style["flex-direction"] = if (isVertical) "column" else "row"
        out.contentNodes.addAll(rule.children.map { subrule ->
            val container = ResultNode("div")
            container.parent = out
            val child = ResultNode()
            child.parent = container
            element.translate(subrule, child)
            container.contentNodes += child
            handleCommonLayoutStuff(subrule, child, container)
            subrule.allAttributes["android:layout_gravity"]?.let { value ->
                when ((if (isVertical) horizontalGravity(value) else verticalGravity(value)).alignDirection) {
                    AlignDirection.START -> container.style["align-self"] = "flex-start"
                    AlignDirection.END -> container.style["align-self"] = "flex-end"
                    AlignDirection.CENTER -> container.style["align-self"] = "center"
                }
            }
            container
        })
    }
    element.handle("FrameLayout") {
        out.style["position"] = "relative"
        out.contentNodes.addAll(rule.children.map { subrule ->
            val container = ResultNode("div")
            container.parent = out
            val child = ResultNode()
            child.parent = container
            element.translate(subrule, child)
            container.contentNodes += child
            handleCommonLayoutStuff(subrule, child, container)
            subrule.allAttributes["android:layout_gravity"]?.let { value ->
                val vert = verticalGravity(value).alignDirection
                val horz = horizontalGravity(value).alignDirection
                container.style["position"] = "absolute"
                if (vert == AlignDirection.CENTER && horz == AlignDirection.CENTER) {
                    container.style["transform"] = "translateX(-50%) translateY(-50%)"
                    container.style["left"] = "50%"
                    container.style["top"] = "50%"
                } else {
                    when (horz) {
                        AlignDirection.START -> container.style["left"] = "0"
                        AlignDirection.END -> container.style["right"] = "0"
                        AlignDirection.CENTER -> {
                            container.style["transform"] = "translateX(-50%)"
                            container.style["left"] = "50%"
                        }
                    }
                    when (vert) {
                        AlignDirection.START -> container.style["top"] = "0"
                        AlignDirection.END -> container.style["bottom"] = "0"
                        AlignDirection.CENTER -> {
                            container.style["transform"] = "translateY(-50%)"
                            container.style["top"] = "50%"
                        }
                    }
                }
            }
            container
        })
    }
    element.handle("ScrollView") {
        out.style["overflow-y"] = "scroll";
        out.style["overflow-x"] = "hidden";
        defer("FrameLayout")
    }
    element.handle("ViewFlipper") {
        out.classes.add("khrysalis-view-flipper")
        defer("FrameLayout")
    }

    attribute.handle("android:padding") {
        rule.value.asCssDimension()?.let { out.style["padding"] = it }
    }
    attribute.handle("android:paddingLeft") {
        rule.value.asCssDimension()?.let { out.style["padding-left"] = it }
    }
    attribute.handle("android:paddingRight") {
        rule.value.asCssDimension()?.let { out.style["padding-right"] = it }
    }
    attribute.handle("android:paddingStart") {
        rule.value.asCssDimension()?.let { out.style["padding-left"] = it }
    }
    attribute.handle("android:paddingEnd") {
        rule.value.asCssDimension()?.let { out.style["padding-right"] = it }
    }
    attribute.handle("android:paddingTop") {
        rule.value.asCssDimension()?.let { out.style["padding-top"] = it }
    }
    attribute.handle("android:paddingBottom") {
        rule.value.asCssDimension()?.let { out.style["padding-bottom"] = it }
    }
}

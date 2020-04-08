package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.utils.*


internal fun HtmlTranslator2.layout() {

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
                    val converted = convertDimension(value)
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
                    val converted = convertDimension(value)
                    child.style["height"] = converted
                    container.style["height"] = converted
                }
            }
        }
        rule.allAttributes["android:layout_margin"]?.let { value -> container.style["padding"] = convertDimension(value) }
        rule.allAttributes["android:layout_marginLeft"]?.let { value ->
            container.style["padding-left"] = convertDimension(value)
        }
        rule.allAttributes["android:layout_marginRight"]?.let { value ->
            container.style["padding-right"] = convertDimension(value)
        }
        rule.allAttributes["android:layout_marginStart"]?.let { value ->
            container.style["padding-left"] = convertDimension(value)
        }
        rule.allAttributes["android:layout_marginEnd"]?.let { value ->
            container.style["padding-right"] = convertDimension(value)
        }
        rule.allAttributes["android:layout_marginTop"]?.let { value ->
            container.style["padding-top"] = convertDimension(value)
        }
        rule.allAttributes["android:layout_marginBottom"]?.let { value ->
            container.style["padding-bottom"] = convertDimension(value)
        }
        rule.allAttributes["android:layout_weight"]?.let { value ->
            container.style["flex-grow"] = value
        }

    }
    element.handle("LinearLayout") {
        out.classes.add("khrysalis-" + rule.name.kabobCase())
        val isVertical = rule.allAttributes["android:orientation"] == "vertical"
        out.style["flex-direction"] = if(isVertical) "column" else "row"
        out.contentNodes.addAll(rule.children.map { subrule ->
            val container = ResultNode("div")
            val child = ResultNode().apply { element.translate(subrule, this) }
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
        out.classes.add("khrysalis-" + rule.name.kabobCase())
        out.contentNodes.addAll(rule.children.map { subrule ->
            val container = ResultNode("div")
            val child = ResultNode().apply { element.translate(subrule, this) }
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
        out.classes.add("khrysalis-scroll-view")
        defer("FrameLayout")
    }
    element.handle("ViewFlipper") {
        out.classes.add("khrysalis-view-flipper")
        defer("FrameLayout")
    }

    attribute.handle("android:padding") {
        val value = rule.value
        out.style["padding"] = convertDimension(value)
    }
    attribute.handle("android:paddingLeft") {
        val value = rule.value
        out.style["padding-left"] = convertDimension(value)
    }
    attribute.handle("android:paddingRight") {
        val value = rule.value
        out.style["padding-right"] = convertDimension(value)
    }
    attribute.handle("android:paddingStart") {
        val value = rule.value
        out.style["padding-left"] = convertDimension(value)
    }
    attribute.handle("android:paddingEnd") {
        val value = rule.value
        out.style["padding-right"] = convertDimension(value)
    }
    attribute.handle("android:paddingTop") {
        val value = rule.value
        out.style["padding-top"] = convertDimension(value)
    }
    attribute.handle("android:paddingBottom") {
        val value = rule.value
        out.style["padding-bottom"] = convertDimension(value)
    }
}

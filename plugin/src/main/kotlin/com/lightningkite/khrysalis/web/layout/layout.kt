package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.asCssDimension
import com.lightningkite.khrysalis.web.attributeAsCssDimension


internal fun HtmlTranslator.layout() {

    element.handle("LinearLayout") {
        out.style["display"] = "flex"
        val isVertical = rule.allAttributes["android:orientation"] == "vertical"
        out.style["flex-direction"] = if (isVertical) "column" else "row"
        rule.allAttributes["android:gravity"]?.let { value ->
            if (isVertical) horizontalGravity(value) else verticalGravity(value)
        }
            ?.alignDirection
            ?.let {
                out.style["align-items"] = when (it) {
                    AlignDirection.START -> "flex-start"
                    AlignDirection.END -> "flex-end"
                    AlignDirection.CENTER -> "center"
                }
            }
        rule.allAttributes["android:gravity"]?.let { value ->
            if (isVertical) verticalGravity(value) else horizontalGravity(value)
        }
            ?.alignDirection
            ?.let {
                out.style["justify-content"] = when (it) {
                    AlignDirection.START -> "flex-start"
                    AlignDirection.END -> "flex-end"
                    AlignDirection.CENTER -> "center"
                }
            }
        out.contentNodes.addAll(rule.children.map { subrule ->
            val child = ResultNode()
            child.parent = out
            element.translate(subrule, child)

            val childGravityString = subrule.allAttributes["android:layout_gravity"]
            val horz = childGravityString?.let { horizontalGravity(it) }?.alignDirection
            val vert = childGravityString?.let { verticalGravity(it) }?.alignDirection
            val otherDirection = if(isVertical) horz else vert
            val otherDirectionSize = if(isVertical) subrule.allAttributes["android:layout_width"] else subrule.allAttributes["android:layout_height"]
            if (otherDirectionSize == "match_parent") {
                child.style["align-self"] = "stretch"
            } else { otherDirection?.name?.toLowerCase()?.let { child.style["align-self"] = it } }
            val hasWeight = subrule.allAttributes["android:layout_weight"] != null
            if(hasWeight && !isVertical) {
                //Use weight instead to define size
            } else {
                subrule.allAttributes["android:layout_width"]?.let { value ->
                    when (value) {
                        "match_parent", "wrap_content" -> {
                        }
                        else -> {
                            val converted = value.asCssDimension() ?: "failedConversion"
                            child.style["width"] = converted
                        }
                    }
                }
            }
            if(hasWeight && isVertical) {
                //Use weight instead to define size
            } else {
                subrule.allAttributes["android:layout_height"]?.let { value ->
                    when (value) {
                        "match_parent", "wrap_content" -> {
                        }
                        else -> {
                            val converted = value.asCssDimension() ?: "failedConversion"
                            child.style["height"] = converted
                        }
                    }
                }
            }
            subrule.attributeAsCssDimension("android:layout_margin")?.let { value ->
                child.style["margin"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginLeft")?.let { value ->
                child.style["margin-left"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginRight")?.let { value ->
                child.style["margin-right"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginStart")?.let { value ->
                child.style["margin-left"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginEnd")?.let { value ->
                child.style["margin-right"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginTop")?.let { value ->
                child.style["margin-top"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginBottom")?.let { value ->
                child.style["margin-bottom"] = value
            }
            subrule.allAttributes["android:layout_weight"]?.let { value ->
                child.style["flex-grow"] = value
                child.style["flex-shrink"] = value
                child.style["flex-basis"] = "0"
            }
            child
        })
    }
    element.handle("FrameLayout") {
        out.classes.add("butterfly-frame")
        if(!out.style.containsKey("z-index")) {
            out.style["z-index"] = "0"
        }
        val startIndex = out.style["z-index"]?.toIntOrNull() ?: 0
        out.contentNodes.addAll(rule.children.mapIndexed { index, subrule ->
            val child = ResultNode()
            child.parent = out
            child.style["z-index"] = index.plus(1 + startIndex).toString()
            element.translate(subrule, child)
            val childGravityString = subrule.allAttributes["android:layout_gravity"]
            val horz = (childGravityString?.let { horizontalGravity(it) }
                ?: Gravity.START_LOCAL).alignDirection
            val vert = (childGravityString?.let { verticalGravity(it) }
                ?: Gravity.START_LOCAL).alignDirection

            if (subrule.allAttributes["android:layout_width"] == "match_parent") {
                child.style["justify-self"] = "stretch"
            } else { child.style["justify-self"] = horz.name.toLowerCase() }
            if (subrule.allAttributes["android:layout_height"] == "match_parent") {
                child.style["align-self"] = "stretch"
            } else { child.style["align-self"] = vert.name.toLowerCase() }
            subrule.allAttributes["android:layout_width"]?.let { value ->
                when (value) {
                    "match_parent", "wrap_content" -> {
                    }
                    else -> {
                        val converted = value.asCssDimension() ?: "failedConversion"
                        child.style["width"] = converted
                    }
                }
            }
            subrule.allAttributes["android:layout_height"]?.let { value ->
                when (value) {
                    "match_parent", "wrap_content" -> {
                    }
                    else -> {
                        val converted = value.asCssDimension() ?: "failedConversion"
                        child.style["height"] = converted
                    }
                }
            }
            subrule.attributeAsCssDimension("android:layout_margin")?.let { value ->
                child.style["margin"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginLeft")?.let { value ->
                child.style["margin-left"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginRight")?.let { value ->
                child.style["margin-right"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginStart")?.let { value ->
                child.style["margin-left"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginEnd")?.let { value ->
                child.style["margin-right"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginTop")?.let { value ->
                child.style["margin-top"] = value
            }
            subrule.attributeAsCssDimension("android:layout_marginBottom")?.let { value ->
                child.style["margin-bottom"] = value
            }
            child
        })
    }
    element.handle("ScrollView") {
        defer("FrameLayout")
        out.classes.add("butterfly-scroll-view")
        out.style["overflow-y"] = "auto";
        out.style["overflow-x"] = "hidden";
    }
    element.handle("HorizontalScrollView") {
        defer("FrameLayout")
        out.classes.add("butterfly-scroll-view")
        out.style["overflow-y"] = "hidden";
        out.style["overflow-x"] = "auto";
    }
    element.handle("ViewFlipper") {
        out.classes.add("butterfly-view-flipper")
        defer("FrameLayout")
    }
    element.handle("com.lightningkite.butterfly.views.widget.SwapView") {
        out.name = "div"
        out.classes += "butterfly-swap"
    }

    element.handle("androidx.viewpager.widget.ViewPager") {
        out.name = "div"
        out.classes += "butterfly-pager"
        out.contentNodes += ResultNode("div").apply {
            classes += "butterfly-pager-content"
        }
        out.contentNodes += ResultNode("button").apply {
            classes += "butterfly-pager-left"
            contentNodes.add("←")
        }
        out.contentNodes += ResultNode("button").apply {
            classes += "butterfly-pager-right"
            contentNodes.add("→")
        }
    }
    element.handle("RadioGroup") {
        out.other["RadioGroupId"] = "radioGroup_${idNumber.getAndIncrement()}"
        defer("LinearLayout")
    }
    element.handle("com.lightningkite.butterfly.views.widget.VerticalRecyclerView") {
        defer("androidx.recyclerview.widget.RecyclerView")
    }
    element.handle("androidx.recyclerview.widget.RecyclerView") {
        out.classes += "butterfly-recycler"
        out.style["flex-direction"] = "column"
    }
    element.handle("com.lightningkite.butterfly.views.widget.CustomView") {
        out.name = "canvas"
    }
    element.handle("androidx.swiperefreshlayout.widget.SwipeRefreshLayout") {
        out.classes += "butterfly-refresh"
        out.contentNodes.addAll(rule.children.map {
            val child = ResultNode()
            child.parent = out
            element.translate(it, child)
            child
        })
        out.contentNodes += ResultNode("button").apply {
            classes += "butterfly-refresh-button"
        }
    }
    element.handle("include") {
        out.name = "div"
    }

    attribute.handle("android:padding") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding"] = it }
    }
    attribute.handle("android:paddingLeft") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding-left"] = it }
    }
    attribute.handle("android:paddingRight") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding-right"] = it }
    }
    attribute.handle("android:paddingStart") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding-left"] = it }
    }
    attribute.handle("android:paddingEnd") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding-right"] = it }
    }
    attribute.handle("android:paddingTop") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding-top"] = it }
    }
    attribute.handle("android:paddingBottom") {
        rule.value.asCssDimension()?.let { out.containerNode.style["padding-bottom"] = it }
    }
}

package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.asCssDimension
import com.lightningkite.khrysalis.web.attributeAsCssDimension


internal fun HtmlTranslator.layout() {

    fun handleCommonLayoutStuff(
        rule: XmlNode,
        child: ResultNode,
        container: ResultNode,
        skipMatchWidth: Boolean = false,
        skipMatchHeight: Boolean = false
    ) {
        rule.allAttributes["android:layout_width"]?.let { value ->
            when (value) {
                "match_parent" -> {
                    if (!skipMatchWidth) {
                        child.style["width"] = "100%"
                        container.style["width"] = "100%"
                    }
                }
                "wrap_content" -> {
                }
                else -> {
                    val converted = value.asCssDimension() ?: "failedConversion"
                    rule.allAttributes["android:layout_weight"]?.let {
                        child.style["width"] = "100%"
                        container.style["width"] = converted
                    } ?: run {
                        child.style["width"] = converted
                    }
                }
            }
        }
        rule.allAttributes["android:layout_height"]?.let { value ->
            when (value) {
                "match_parent" -> {
                    if (!skipMatchHeight) {
                        child.style["height"] = "100%"
                        container.style["height"] = "100%"
                    }
                }
                "wrap_content" -> {
                }
                else -> {
                    val converted = value.asCssDimension() ?: "failedConversion"
                    rule.allAttributes["android:layout_weight"]?.let {
                        child.style["height"] = "100%"
                        container.style["height"] = converted
                    } ?: run {
                        child.style["height"] = converted
                    }
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
            container.style["flex-shrink"] = value
        }

    }
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
            val container = ResultNode("div")
            container.classes.add("khrysalis-box")
            container.parent = out
            val child = ResultNode()
            child.parent = container
            element.translate(subrule, child)
            container.contentNodes += child
            val useAlignStretch = if (isVertical)
                subrule.allAttributes["android:layout_width"] == "match_parent"
            else
                subrule.allAttributes["android:layout_height"] == "match_parent"
            if (useAlignStretch) {
                container.style["align-self"] = "stretch"
            } else {
                (subrule.allAttributes["android:layout_gravity"])?.let { value ->
                    if (isVertical) horizontalGravity(value) else verticalGravity(value)
                }
                    ?.alignDirection
                    ?.let {
                        container.style["align-self"] = when (it) {
                            AlignDirection.START -> "flex-start"
                            AlignDirection.END -> "flex-end"
                            AlignDirection.CENTER -> "center"
                        }
                    }
            }
            handleCommonLayoutStuff(
                rule = subrule,
                child = child,
                container = container,
                skipMatchWidth = useAlignStretch,
                skipMatchHeight = useAlignStretch
            )
            container
        })
    }
    element.handle("FrameLayout") {
        out.classes.add("khrysalis-box")
        val supercontainer = ResultNode()
        out.primary = supercontainer
        supercontainer.parent = out
        out.contentNodes.add(supercontainer)
        supercontainer.style["position"] = "relative"
        val parentGravityString = rule.allAttributes["android:gravity"]
        supercontainer.contentNodes.addAll(rule.children.mapIndexed { index, subrule ->
            val container = ResultNode("div")
            container.classes.add("khrysalis-box")
            container.parent = supercontainer
            val child = ResultNode()
            child.parent = container
            element.translate(subrule, child)
            container.contentNodes += child
            val childGravityString = subrule.allAttributes["android:layout_gravity"]
            val horz = (childGravityString?.let { horizontalGravity(it) }
                ?: parentGravityString?.let { horizontalGravity(it) }
                ?: Gravity.START_LOCAL).alignDirection
            val vert = (childGravityString?.let { verticalGravity(it) }
                ?: parentGravityString?.let { verticalGravity(it) }
                ?: Gravity.START_LOCAL).alignDirection

            val relativeSizing =
                rule.allAttributes["android:layout_width"] == "wrap_content" || rule.allAttributes["android:layout_height"] == "wrap_content"
            if (index == 0 && relativeSizing) {
                //Do nothing - things are placed relative to this anyways
                handleCommonLayoutStuff(
                    subrule,
                    child,
                    container,
                    skipMatchWidth = rule.allAttributes["android:layout_width"] == "wrap_content",
                    skipMatchHeight = rule.allAttributes["android:layout_height"] == "wrap_content"
                )
            } else {
                handleCommonLayoutStuff(subrule, child, container)
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
        out.style["overflow-y"] = "auto";
        out.style["overflow-x"] = "hidden";
        defer("FrameLayout")
    }
    element.handle("HorizontalScrollView") {
        out.classes.add("khrysalis-scroll-view")
        out.style["overflow-y"] = "hidden";
        out.style["overflow-x"] = "auto";
        defer("FrameLayout")
    }
    element.handle("ViewFlipper") {
        out.classes.add("khrysalis-view-flipper")
        defer("FrameLayout")
    }
    element.handle("com.lightningkite.butterfly.views.widget.SwapView") {
        out.name = "div"
        out.classes += "khrysalis-swap"
    }

    element.handle("androidx.viewpager.widget.ViewPager") {
        out.name = "div"
        out.classes += "khrysalis-pager"
        out.contentNodes += ResultNode("div").apply {
            classes += "khrysalis-pager-content"
        }
        out.contentNodes += ResultNode("button").apply {
            classes += "khrysalis-pager-left"
            contentNodes.add("←")
        }
        out.contentNodes += ResultNode("button").apply {
            classes += "khrysalis-pager-right"
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
        out.classes += "khrysalis-recycler"
        out.style["flex-direction"] = "column"
    }
    element.handle("com.lightningkite.butterfly.views.widget.CustomView") {
        out.name = "canvas"
    }
    element.handle("androidx.swiperefreshlayout.widget.SwipeRefreshLayout") {
        out.classes += "khrysalis-refresh"
        out.contentNodes.addAll(rule.children.map {
            val child = ResultNode()
            child.parent = out
            element.translate(it, child)
            child
        })
        out.contentNodes += ResultNode("button").apply {
            classes += "khrysalis-refresh-button"
        }
    }
    element.handle("include") {
        out.name = "div"
        out.style["position"] = "relative"
        rule.allAttributes["android:id"]?.removePrefix("@+id/")?.trim()?.let { it ->
            out.attributes["id"] = "view_$it"
            val stackDefault = rule.allAttributes["layout"]?.calcDest()
            val stackDefaultJs = if (stackDefault != null) "'$stackDefault'" else "null"
            out.contentNodes.add(ResultNode("script").apply {
                contentNodes.add("""prototypeSwapViewSetup(document.getElementById('view_$it'), '$it', $stackDefaultJs)""")
            })
        }
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

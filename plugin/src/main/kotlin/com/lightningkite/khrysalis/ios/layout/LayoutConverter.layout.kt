package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.layoutViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.butterfly.views.widget.SwapView", "SwapView", "View") { node -> },
            ViewType("androidx.core.widget.NestedScrollView", "ScrollViewVertical", "ScrollView") { node -> },
            ViewType("ScrollView", "ScrollViewVertical", "View") { node ->
                node.attributeAsBoolean("android:fillViewport")?.let {
                    appendLine("view.fillViewport = $it")
                }
                val child = node.children.first()
                append("view.addSubview(")
                construct(child)
                appendLine(") { view in ")
                writeSetup(child)
                appendLine("}")
            },

            ViewType("HorizontalScrollView", "ScrollViewHorizontal", "View") { node ->
                node.attributeAsBoolean("android:fillViewport")?.let {
                    appendLine("view.fillViewport = $it")
                }
                val child = node.children.first()
                append("view.addSubview(")
                construct(child)
                appendLine(") { view in ")
                writeSetup(child)
                appendLine("}")
            },
            ViewType("LinearLayout", "LinearLayout", "View", handlesPadding = true) { node ->
                val isHorizontal = when (node.allAttributes["android:orientation"]) {
                    "horizontal" -> true
                    "vertical" -> false
                    else -> true
                }
                appendLine("view.orientation = " + if (isHorizontal) ".x" else ".y")

                val dividerStart = node.allAttributes["android:showDividers"]?.contains("beginning") ?: false
                val dividerMiddle = node.allAttributes["android:showDividers"]?.contains("middle") ?: false
                val dividerEnd = node.allAttributes["android:showDividers"]?.contains("end") ?: false

                setPadding(node)

                val defaultGravity = node.allAttributes["android:gravity"]
                appendLine("view.gravity = ${align(null, null, defaultGravity)}")

                val dividerText = if (isHorizontal)
                    node.allAttributes["tools:iosDivider"]
                        ?: "view.addSubview(UIView(), size: CGSize(width: 1, height: 1), gravity: AlignPair(horizontal: .center, vertical: .fill)) { div in div.backgroundColor = .gray }"
                else
                    node.allAttributes["tools:iosDivider"]
                        ?: "view.addSubview(UIView(), size: CGSize(width: 1, height: 1), gravity: AlignPair(horizontal: .fill, vertical: .center)) { div in div.backgroundColor = .gray }"

                appendln()

                if (dividerStart) {
                    appendLine(dividerText)
                }
                node.children.forEachBetween(
                    forItem = { child ->
                        appendLine("view.addSubview(")

                        construct(child)
                        appendLine(",")

                        append("minimumSize: CGSize(width: ")
                        append(child.attributeAsSwiftDimension("android:minWidth") ?: "0")
                        append(", height: ")
                        append(child.attributeAsSwiftDimension("android:minHeight") ?: "0")
                        appendLine("),")

                        append("size: CGSize(width: ")
                        append(child.attributeAsSwiftDimension("android:layout_width") ?: "-1")
                        append(", height: ")
                        append(child.attributeAsSwiftDimension("android:layout_height") ?: "-1")
                        appendLine("),")

                        append(
                            "margin: ${margins(child)}"
                        )
                        appendLine(",")

                        append(
                            "padding: ${padding(child)}"
                        )
                        appendLine(",")

                        append("gravity: ")
                        append(
                            align(
                                width = child.allAttributes["android:layout_width"],
                                height = child.allAttributes["android:layout_height"],
                                gravityStrings = *arrayOf(child.allAttributes["android:layout_gravity"], defaultGravity)
                            )
                        )
                        appendLine(",")

                        append("weight: ")
                        append(child.attributeAsDouble("android:layout_weight")?.toString() ?: "0")
                        appendln()
                        appendLine(") { view in ")
                        writeSetup(child)
                        appendLine("}")
                    },
                    between = {
                        appendln()
                        if (dividerMiddle) {
                            appendLine(dividerText)
                            appendln()
                        }
                    }
                )
                if (dividerEnd) {
                    appendLine(dividerText)
                }
                appendln()
            },

            ViewType("ViewFlipper", "ViewFlipper", "FrameLayout", handlesPadding = true) { node ->

            },
            ViewType("FrameLayout", "FrameLayout", "View", handlesPadding = true) { node ->
                setPadding(node)

                val defaultGravity = node.allAttributes["android:gravity"]

                node.children.forEach { child ->
                    appendLine("view.addSubview(")

                    construct(child)
                    appendLine(",")

                    append("minimumSize: CGSize(width: ")
                    append(child.attributeAsSwiftDimension("android:minWidth") ?: "0")
                    append(", height: ")
                    append(child.attributeAsSwiftDimension("android:minHeight") ?: "0")
                    appendLine("),")

                    append("size: CGSize(width: ")
                    append(child.attributeAsSwiftDimension("android:layout_width") ?: "-1")
                    append(", height: ")
                    append(child.attributeAsSwiftDimension("android:layout_height") ?: "-1")
                    appendLine("),")

                    append(
                        "margin: ${margins(child)}"
                    )
                    appendLine(",")

                    append(
                        "padding: ${padding(child)}"
                    )
                    appendLine(",")

                    append("gravity: ")
                    append(
                        align(
                            width = child.allAttributes["android:layout_width"],
                            height = child.allAttributes["android:layout_height"],
                            gravityStrings = *arrayOf(child.allAttributes["android:layout_gravity"], defaultGravity)
                        )
                    )
                    appendln()
                    appendLine(") { view in ")
                    writeSetup(child)
                    appendLine("}")
                    appendln()

                }
            }
        )
    )

private fun OngoingLayoutConversion.setPadding(node: XmlNode) {
    val defaultPadding = node.attributeAsSwiftDimension("android:padding") ?: 0
    appendLine(
        "view.padding = ${
            uiEdgeInsets(
                (node.attributeAsSwiftDimension("android:paddingTop")
                    ?: defaultPadding).toString(),
                (node.attributeAsSwiftDimension("android:paddingLeft")
                    ?: node.attributeAsSwiftDimension("android:paddingStart")
                    ?: defaultPadding).toString(),
                (node.attributeAsSwiftDimension("android:paddingBottom")
                    ?: defaultPadding).toString(),
                (node.attributeAsSwiftDimension("android:paddingRight")
                    ?: node.attributeAsSwiftDimension("android:paddingEnd")
                    ?: defaultPadding).toString()
            )
        }"
    )
}

fun align(width: String?, height: String?, vararg gravityStrings: String?): String {
    val gravityArgs = gravityStrings.flatMap { it?.split("|") ?: listOf() }
    val translatedHorz = if (width == "match_parent") "Fill" else gravityArgs.asSequence().mapNotNull {
        when (it) {
            "center", "center_horizontal" -> "Center"
            "left", "start" -> "Left"
            "right", "end" -> "Right"
            else -> null
        }
    }.firstOrNull() ?: "Left"
    val translatedVert = if (height == "match_parent") ".fill" else gravityArgs.asSequence().mapNotNull {
        when (it) {
            "center", "center_vertical" -> ".center"
            "top" -> ".top"
            "bottom" -> ".bottom"
            else -> null
        }
    }.firstOrNull() ?: ".top"
    return translatedVert + translatedHorz
}

fun alignFill(vararg gravityStrings: String?): String {
    val raw = gravityStrings.flatMap { it?.split("|") ?: listOf() }
    var gravity = 0
    if (raw.contains("center"))
        gravity = gravity or SafePaddingFlags.ALL
    if (raw.contains("all"))
        gravity = gravity or SafePaddingFlags.ALL
    if (raw.contains("center_horizontal"))
        gravity = gravity or SafePaddingFlags.LEFT or SafePaddingFlags.RIGHT
    if (raw.contains("horizontal"))
        gravity = gravity or SafePaddingFlags.LEFT or SafePaddingFlags.RIGHT
    if (raw.contains("left"))
        gravity = gravity or SafePaddingFlags.LEFT
    if (raw.contains("right"))
        gravity = gravity or SafePaddingFlags.RIGHT
    if (raw.contains("center_vertical"))
        gravity = gravity or SafePaddingFlags.TOP or SafePaddingFlags.BOTTOM
    if (raw.contains("vertical"))
        gravity = gravity or SafePaddingFlags.TOP or SafePaddingFlags.BOTTOM
    if (raw.contains("top"))
        gravity = gravity or SafePaddingFlags.TOP
    if (raw.contains("bottom"))
        gravity = gravity or SafePaddingFlags.BOTTOM
    return "." + when {
        gravity and SafePaddingFlags.TOP != 0 && gravity and SafePaddingFlags.BOTTOM != 0 -> "fill"
        gravity and SafePaddingFlags.TOP != 0 -> "top"
        gravity and SafePaddingFlags.BOTTOM != 0 -> "bottom"
        else -> "center"
    } + when {
        gravity and SafePaddingFlags.LEFT != 0 && gravity and SafePaddingFlags.RIGHT != 0 -> "Fill"
        gravity and SafePaddingFlags.LEFT != 0 -> "Left"
        gravity and SafePaddingFlags.RIGHT != 0 -> "Right"
        else -> "Center"
    }
}

fun uiEdgeInsets(
    top: String,
    left: String,
    bottom: String,
    right: String
): String = "UIEdgeInsets(top: $top, left: $left, bottom: $bottom, right: $right)"

fun OngoingLayoutConversion.margins(child: XmlNode): String {
    val defaultMargin = child.attributeAsSwiftDimension("android:layout_margin") ?: 0
    val top = (child.attributeAsSwiftDimension("android:layout_marginTop")
        ?: defaultMargin).toString()
    val left = (child.attributeAsSwiftDimension("android:layout_marginLeft")
        ?: child.attributeAsSwiftDimension("android:layout_marginStart")
        ?: defaultMargin).toString()
    val bottom = (child.attributeAsSwiftDimension("android:layout_marginBottom")
        ?: defaultMargin).toString()
    val right = (child.attributeAsSwiftDimension("android:layout_marginRight")
        ?: child.attributeAsSwiftDimension("android:layout_marginEnd")
        ?: defaultMargin).toString()
    return uiEdgeInsets(top, left, bottom, right)
}

fun OngoingLayoutConversion.padding(child: XmlNode): String {
    if (this.converter.viewTypes[child.name]?.handlesPadding != true) {
        val defaultPadding = child.attributeAsSwiftDimension("android:padding") ?: 0
        val top = (child.attributeAsSwiftDimension("android:paddingTop")
            ?: defaultPadding).toString()
        val left = (child.attributeAsSwiftDimension("android:paddingLeft")
            ?: child.attributeAsSwiftDimension("android:paddingStart")
            ?: defaultPadding).toString()
        val bottom = (child.attributeAsSwiftDimension("android:paddingBottom")
            ?: defaultPadding).toString()
        val right = (child.attributeAsSwiftDimension("android:paddingRight")
            ?: child.attributeAsSwiftDimension("android:paddingEnd")
            ?: defaultPadding).toString()
        return uiEdgeInsets(top, left, bottom, right)
    } else {
        return "UIEdgeInsets.zero"
    }
}

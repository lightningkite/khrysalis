package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*

val LayoutConverter.Companion.layoutViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.kwift.views.android.SwapView", "UIView", "View") { node -> },
            ViewType("androidx.core.widget.NestedScrollView", "UIScrollView", "ScrollView") { node -> },
            ViewType("ScrollView", "UIScrollView", "View") { node ->
                val child = node.children.first()
                append("view.addVerticalSubview(")
                construct(child)
                appendln(") { view in ")
                writeSetup(child)
                appendln("}")
            },

            ViewType("HorizontalScrollView", "UIScrollView", "View") { node ->
                val child = node.children.first()
                append("view.addHorizontalSubview(")
                construct(child)
                appendln(") { view in ")
                writeSetup(child)
                appendln("}")
            },
            ViewType("LinearLayout", "LinearLayout", "View") { node ->
                val isHorizontal = when (node.attributes["android:orientation"]) {
                    "horizontal" -> true
                    "vertical" -> false
                    else -> true
                }
                appendln("view.orientation = " + if (isHorizontal) ".x" else ".y")

                val dividerStart = node.attributes["android:showDividers"]?.contains("beginning") ?: false
                val dividerMiddle = node.attributes["android:showDividers"]?.contains("middle") ?: false
                val dividerEnd = node.attributes["android:showDividers"]?.contains("end") ?: false

                val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
                appendln(
                    "view.padding = ${uiEdgeInsets(
                        (node.attributeAsDimension("android:paddingTop")
                            ?: defaultPadding).toString(),
                        (node.attributeAsDimension("android:paddingLeft")
                            ?: node.attributeAsDimension("android:paddingStart")
                            ?: defaultPadding).toString(),
                        (node.attributeAsDimension("android:paddingBottom")
                            ?: defaultPadding).toString(),
                        (node.attributeAsDimension("android:paddingRight")
                            ?: node.attributeAsDimension("android:paddingEnd")
                            ?: defaultPadding).toString()
                    )}"
                )

                val defaultGravity = node.attributes["android:gravity"]

                val dividerText = if (isHorizontal)
                    node.attributes["tools:iosDivider"]
                        ?: "view.addSubview(UIView(), size: CGSize(width: 1, height: 1), gravity: AlignPair(horizontal: .center, vertical: .fill)) { div in div.backgroundColor = .gray }"
                else
                    node.attributes["tools:iosDivider"]
                        ?: "view.addSubview(UIView(), size: CGSize(width: 1, height: 1), gravity: AlignPair(horizontal: .fill, vertical: .center)) { div in div.backgroundColor = .gray }"

                appendln()

                if (dividerStart) {
                    appendln(dividerText)
                }
                node.children.forEachBetween(
                    forItem = { child ->
                        appendln("view.addSubview(")

                        construct(child)
                        appendln(",")

                        append("minimumSize: CGSize(width: ")
                        append(child.attributeAsDimension("android:minWidth") ?: "0")
                        append(", height: ")
                        append(child.attributeAsDimension("android:minHeight") ?: "0")
                        appendln("),")

                        append("size: CGSize(width: ")
                        append(child.attributeAsDimension("android:layout_width") ?: "0")
                        append(", height: ")
                        append(child.attributeAsDimension("android:layout_height") ?: "0")
                        appendln("),")

                        val defaultMargin = child.attributeAsDimension("android:layout_margin") ?: 0
                        append(
                            "margin: ${uiEdgeInsets(
                                (child.attributeAsDimension("android:layout_marginTop")
                                    ?: defaultMargin).toString(),
                                (child.attributeAsDimension("android:layout_marginLeft")
                                    ?: child.attributeAsDimension("android:layout_marginStart")
                                    ?: defaultMargin).toString(),
                                (child.attributeAsDimension("android:layout_marginBottom")
                                    ?: defaultMargin).toString(),
                                (child.attributeAsDimension("android:layout_marginRight")
                                    ?: child.attributeAsDimension("android:layout_marginEnd")
                                    ?: defaultMargin).toString()
                            )}"
                        )
                        appendln(",")

                        append("gravity: ")
                        append(
                            align(
                                width = child.attributes["android:layout_width"],
                                height = child.attributes["android:layout_height"],
                                gravityStrings = *arrayOf(child.attributes["android:layout_gravity"], defaultGravity)
                            )
                        )
                        appendln(",")

                        append("weight: ")
                        append(child.attributeAsDouble("android:layout_weight")?.toString() ?: "0")
                        appendln()
                        appendln(") { view in ")
                        writeSetup(child)
                        appendln("}")
                    },
                    between = {
                        appendln()
                        if (dividerMiddle) {
                            appendln(dividerText)
                            appendln()
                        }
                    }
                )
                if (dividerEnd) {
                    appendln(dividerText)
                }
                appendln()
            },

            ViewType("FrameLayout", "FrameLayout", "View") { node ->
                val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
                appendln(
                    "view.padding = ${uiEdgeInsets(
                        (node.attributeAsDimension("android:paddingTop")
                            ?: defaultPadding).toString(),
                        (node.attributeAsDimension("android:paddingLeft")
                            ?: node.attributeAsDimension("android:paddingStart")
                            ?: defaultPadding).toString(),
                        (node.attributeAsDimension("android:paddingBottom")
                            ?: defaultPadding).toString(),
                        (node.attributeAsDimension("android:paddingRight")
                            ?: node.attributeAsDimension("android:paddingEnd")
                            ?: defaultPadding).toString()
                    )}"
                )

                val defaultGravity = node.attributes["android:gravity"]

                node.children.forEach { child ->
                    appendln("view.addSubview(")

                    construct(child)
                    appendln(",")

                    append("minimumSize: CGSize(width: ")
                    append(child.attributeAsDimension("android:minWidth") ?: "0")
                    append(", height: ")
                    append(child.attributeAsDimension("android:minHeight") ?: "0")
                    appendln("),")

                    append("size: CGSize(width: ")
                    append(child.attributeAsDimension("android:layout_width") ?: "0")
                    append(", height: ")
                    append(child.attributeAsDimension("android:layout_height") ?: "0")
                    appendln("),")

                    val defaultMargin = child.attributeAsDimension("android:layout_margin") ?: 0
                    append(
                        "margin: ${uiEdgeInsets(
                            (child.attributeAsDimension("android:layout_marginTop")
                                ?: defaultMargin).toString(),
                            (child.attributeAsDimension("android:layout_marginLeft")
                                ?: child.attributeAsDimension("android:layout_marginStart")
                                ?: defaultMargin).toString(),
                            (child.attributeAsDimension("android:layout_marginBottom")
                                ?: defaultMargin).toString(),
                            (child.attributeAsDimension("android:layout_marginRight")
                                ?: child.attributeAsDimension("android:layout_marginEnd")
                                ?: defaultMargin).toString()
                        )}"
                    )
                    appendln(",")

                    append("gravity: ")
                    append(
                        align(
                            width = child.attributes["android:layout_width"],
                            height = child.attributes["android:layout_height"],
                            gravityStrings = *arrayOf(child.attributes["android:layout_gravity"], defaultGravity)
                        )
                    )
                    appendln()
                    appendln(") { view in ")
                    writeSetup(child)
                    appendln("}")
                    appendln()

                }
            }
        )
    )

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

fun uiEdgeInsets(
    top: String,
    left: String,
    bottom: String,
    right: String
): String = "UIEdgeInsets(top: $top, left: $left, bottom: $bottom, right: $right)"

package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*
import kotlin.math.PI

val LayoutConverter.Companion.displayViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("View", "UIView") { node ->
                node.attributes["android:id"]?.let { raw ->
                    val name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase()
                    appendln("self.$name = view")
                    bindings[name] = converter.viewTypes[node.name]?.iosName ?: "UIView"
                }
                node.attributes["android:background"]?.let { raw ->
                    when {
                        raw.startsWith("@drawable/") -> {
                            node.attributeAsLayer("android:background", "view")!!.let {
                                appendln("view.background = $it")
                            }
                        }
                        raw.startsWith("@mipmap/") -> {
                            val drawableName = raw.removePrefix("@mipmap/")
                            appendln("if let image = UIImage(named: \"$drawableName\") {")
                            appendln("view.backgroundColor = UIColor(patternImage: image)")
                            appendln("}")
                        }
                        raw.startsWith("@color/") -> {
                            val colorName = raw.removePrefix("@color/")
                            appendln("view.backgroundColor = ResourcesColors.${colorName.camelCase()}")
                        }
                        raw.startsWith("@android:color/") -> {
                            val colorName = raw.removePrefix("@android:color/")
                            appendln("view.backgroundColor = ResourcesColors.${colorName.camelCase()}")
                        }
                        else -> {
                        }
                    }
                }
                node.attributeAsDimension("android:elevation")?.let {
                    appendln("view.layer.masksToBounds = false")
                    appendln("view.layer.shadowColor = UIColor.black.cgColor")
                    appendln("view.layer.shadowOffset = CGSize(width: 0, height: $it)")
                    appendln("view.layer.shadowRadius = $it")
                    appendln("view.layer.shadowOpacity = 0.24")
                }

                node.attributeAsDouble("android:rotation")?.let {
                    appendln("view.transform = CGAffineTransform(rotationAngle: ${it * PI / 180.0})")
                }
            },
            ViewType("Space", "UIView", "View") {},
            ViewType("ImageView", "UIImageView", "View") { node ->
                node.attributeAsImage("android:src")?.let { text ->
                    appendln("view.image = $text")
                }
                appendln(
                    "view.contentMode = ${when (node.attributes["android:scaleType"]) {
                        "fitXY" -> ".scaleToFill"
                        "centerCrop" -> ".scaleAspectFill"
                        "centerInside" -> ".scaleAspectFit"
                        else -> ".scaleAspectFit"
                    }}"
                )
//            node.attributeAsDouble("android:rotation")?.let{
//                "view.transform = CGAffineTransform(rotationAngle: ${it * PI / 180.0}"
//            }
            },
            ViewType("de.hdodenhof.circleimageview.CircleImageView", "UIImageView", "ImageView") { node ->

                appendln("view.addOnLayoutSubviews { [weak view] in")
                appendln("if let view = view {")
                appendln("    view.layer.cornerRadius = view.frame.size.width / 2;")
                appendln("    view.contentMode = .scaleAspectFill")
                appendln("}")
                appendln("}")
                appendln("view.clipsToBounds = true")
            },


            ViewType("TextView", "UILabel", "View") { node ->
                handleCommonText(node)
            }
        )
    )


internal fun OngoingLayoutConversion.handleCommonText(node: XmlNode, viewHandle: String = "view", controlView: String? = null) {
    node.attributeAsString("android:text")?.let { text ->
        if (node.attributeAsBoolean("android:textAllCaps") == true) {
            appendln("$viewHandle.text = $text.toUpperCase()")
        } else {
            appendln("$viewHandle.text = $text")
        }
    }
    node.attributeAsDouble("android:lineSpacingMultiplier")?.let { lineSpacingMultiplier ->
        appendln("$viewHandle.lineSpacingMultiplier = $lineSpacingMultiplier")
    }
    val lines = node.attributeAsInt("android:maxLines")
    appendln("$viewHandle.numberOfLines = ${lines ?: 0}")
    val size = node.attributeAsDimension("android:textSize") ?: "12"
    val fontStyles = node.attributes["android:textStyle"]?.split('|') ?: listOf()
    appendln("$viewHandle.font = UIFont.get(size: $size, style: [${fontStyles.joinToString { "\"$it\"" }}])")

    if(controlView!= null) {
        node.setToColorGivenControl("android:textColor") {
            appendln("$viewHandle.textColor = $it")
        }
    } else {
        node.attributeAsColor("android:textColor")?.let {
            appendln("$viewHandle.textColor = $it")
        }
    }
    node.attributes["android:gravity"]?.let {
        it.split('|')
            .asSequence()
            .mapNotNull { horizontalGravityWords[it] }
            .firstOrNull()
            ?.let {
                val fixed = when (it) {
                    ".start" -> ".left"
                    ".center" -> ".center"
                    ".end" -> ".right"
                    else -> ".left"
                }
                appendln("$viewHandle.textAlignment = $fixed")
            }
        it.split('|')
            .asSequence()
            .mapNotNull { verticalGravityWords[it] }
            .firstOrNull()
            ?.let {
                val fixed = when (it) {
                    ".start" -> ".none"
                    ".center" -> ".alignCenters"
                    ".end" -> ".alignBaselines"
                    else -> ".none"
                }
                appendln("$viewHandle.baselineAdjustment = $fixed")
            }
    }
}


internal val horizontalGravityWords = mapOf(
    "left" to ".start",
    "right" to ".end",
    "center_horizontal" to ".center",
    "start" to ".start",
    "end" to ".end",
    "center" to ".center"
)
internal val verticalGravityWords = mapOf(
    "top" to ".start",
    "bottom" to ".end",
    "center_vertical" to ".center",
    "center" to ".center"
)

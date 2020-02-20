package com.lightningkite.khrysalis.drawables

import com.lightningkite.khrysalis.utils.*
import java.lang.Appendable


fun convertShapeDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing shape $name")
    when (node.attributes["android:shape"]) {
        "oval" -> {
            val width = 100.0
            val height = 100.0
            with(out) {
                node.children.find { it.name == "gradient" }?.let {
                    appendln("static func $name(_ view: UIView? = nil) -> CAGradientLayer {")
                    appendln("    let mask = CAShapeLayer()")
                    appendln("    mask.path = CGPath(ellipseIn: CGRect(x: 0, y: 0, width: $width, height: $height), transform: nil)")
                    appendln("    let gradient = CAGradientLayer()")
                    appendln("    gradient.mask = mask")
                    val colors = listOfNotNull(
                        it.attributeAsColor("android:startColor"),
                        it.attributeAsColor("android:centerColor"),
                        it.attributeAsColor("android:endColor")
                    )
                    appendln("    gradient.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                    val angle = it.attributeAsInt("android:angle") ?: 0
                    appendln("    gradient.setGradientAngle(degrees: $angle)")
                    appendln("    return gradient")
                    appendln("}")
                } ?: run {
                    appendln("static func $name(_ view: UIView? = nil) -> CAShapeLayer {")
                    appendln("    let layer = CAShapeLayer()")
                    appendln("    layer.path = CGPath(ellipseIn: CGRect(x: 0, y: 0, width: $width, height: $height), transform: nil)")
                    node.children.find { it.name == "stroke" }?.let {
                        appendln("    layer.borderWidth = ${it.attributeAsDimension("android:width") ?: "0"}")
                        appendln(
                            "    layer.strokeColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor"
                        )
                    }
                    node.children.find { it.name == "solid" }?.let {
                        appendln(
                            "    layer.fillColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor"
                        )
                    }
                    appendln("    layer.bounds.size = CGSize(width: $width, height: $height)")
                    appendln("    layer.scaleOverResize = true")
                    appendln("    return layer")
                    appendln("}")
                }
            }
        }
        "rectangle" -> {
            val className = if (node.children.any { it.name == "gradient" }) "CAGradientLayer" else "CALayer"
            with(out) {
                appendln("static func $name(_ view: UIView? = nil) -> $className {")
                appendln("    let layer = $className()")
                node.children.find { it.name == "stroke" }?.let {
                    appendln("    layer.borderWidth = ${it.attributeAsDimension("android:width") ?: "0"}")
                    appendln(
                        "    layer.borderColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor"
                    )
                }
                node.children.find { it.name == "solid" }?.let {
                    appendln(
                        "    layer.backgroundColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor"
                    )
                }
                node.children.find { it.name == "corners" }?.let { corners ->
                    corners.attributeAsDimension("android:radius")?.let {
                        appendln("    layer.maxCornerRadius = $it")
                        appendln("    layer.cornerRadius = $it")
                    } ?: run {
                        val radius = corners.attributeAsDimension("android:bottomLeftRadius")
                            ?: corners.attributeAsDimension("android:topLeftRadius")
                            ?: corners.attributeAsDimension("android:bottomRightRadius")
                            ?: corners.attributeAsDimension("android:topRightRadius")
                        if (radius != null) {
                            appendln("    layer.cornerRadius = $radius")
                            append("    layer.maskedCorners = [")
                            append(
                                mapOf(
                                    "android:bottomLeftRadius" to ".layerMinXMaxYCorner",
                                    "android:topLeftRadius" to ".layerMinXMinYCorner",
                                    "android:bottomRightRadius" to ".layerMaxXMaxYCorner",
                                    "android:topRightRadius" to ".layerMaxXMinYCorner"
                                ).filterKeys { corners.attributes.containsKey(it) }.values.joinToString()
                            )
                            appendln("]")
                        }
                    }
                }
                node.children.find { it.name == "gradient" }?.let {
                    val colors = listOfNotNull(
                        it.attributeAsColor("android:startColor"),
                        it.attributeAsColor("android:centerColor"),
                        it.attributeAsColor("android:endColor")
                    )
                    appendln("    layer.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                    val angle = it.attributeAsInt("android:angle") ?: 0
                    appendln("    layer.setGradientAngle(degrees: $angle)")
                }
                appendln("    layer.bounds.size = CGSize(width: 100, height: 100)")
                appendln("    return layer")
                appendln("}")
            }
        }
    }
}

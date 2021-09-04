package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import com.lightningkite.khrysalis.ios.layout.setToColor
import java.lang.Appendable


fun convertShapeDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing shape $name")
    when (node.allAttributes["android:shape"]) {
        "oval" -> {
            val width = 100.0
            val height = 100.0
            with(out) {
                node.children.find { it.name == "gradient" }?.let {
                    appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
                    appendLine("    let mask = CAShapeLayer()")
                    appendLine("    mask.path = CGPath(ellipseIn: CGRect(x: 0, y: 0, width: $width, height: $height), transform: nil)")
                    appendLine("    let gradient = CAGradientLayer()")
                    appendLine("    gradient.mask = mask")
                    val colors = listOfNotNull(
                        it.attributeAsSwiftColor("android:startColor"),
                        it.attributeAsSwiftColor("android:centerColor"),
                        it.attributeAsSwiftColor("android:endColor")
                    )
                    appendLine("    gradient.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                    val angle = it.attributeAsInt("android:angle") ?: 0
                    appendLine("    gradient.setGradientAngle(degrees: $angle)")
                    appendLine("    gradient.onResize.subscribeBy { [weak mask] (rect) in ")
                    appendLine("        mask?.path = CGPath(ellipseIn: rect, transform: nil)")
                    appendLine("    }")
                    appendLine("    return gradient")
                    appendLine("}")
                } ?: run {
                    appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
                    appendLine("    let layer = CAShapeLayer()")
                    appendLine("    layer.path = CGPath(ellipseIn: CGRect(x: 0, y: 0, width: $width, height: $height), transform: nil)")
                    node.children.find { it.name == "stroke" }?.let {
                        appendLine("    layer.lineWidth = ${it.attributeAsSwiftDimension("android:width") ?: "0"}")
                        setToColor(it, "android:color") { it, s ->
                            appendLine(
                                "    layer.strokeColor = $it.cgColor"
                            )
                        }
                    }
                    node.children.find { it.name == "solid" }?.let {
                        setToColor(it, "android:color") { it, s ->
                            appendLine(
                                "    layer.fillColor = $it.cgColor"
                            )
                        }
                    } ?: run {
                        appendLine("    layer.fillColor = nil")
                    }
                    appendLine("    layer.onResize.subscribeBy { [weak layer] (rect) in ")
                    appendLine("        layer?.path = CGPath(ellipseIn: rect, transform: nil)")
                    appendLine("    }")
                    appendLine("    return layer")
                    appendLine("}")
                }
            }
        }
        else -> {
            val className = if (node.children.any { it.name == "gradient" }) "CAGradientLayer" else "CALayer"
            with(out) {
                appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
                appendLine("    let layer = $className()")
                node.children.find { it.name == "stroke" }?.let {
                    appendLine("    layer.borderWidth = ${it.attributeAsSwiftDimension("android:width") ?: "0"}")
                    setToColor(it, "android:color") { it, s ->
                        appendLine(
                            "    layer.borderColor = $it.cgColor"
                        )
                    }
                }
                node.children.find { it.name == "solid" }?.let {
                    setToColor(it, "android:color") { it, s ->
                        appendLine(
                            "    layer.backgroundColor = $it.cgColor"
                        )
                    }
                } ?: node.children.find { it.name == "gradient" }?.let {
                    val colors = listOfNotNull(
                        it.attributeAsSwiftColor("android:startColor"),
                        it.attributeAsSwiftColor("android:centerColor"),
                        it.attributeAsSwiftColor("android:endColor")
                    )
                    appendLine("    layer.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                    val angle = it.attributeAsInt("android:angle") ?: 0
                    appendLine("    layer.setGradientAngle(degrees: $angle)")
                }
                node.children.find { it.name == "corners" }?.let { corners ->
                    corners.attributeAsSwiftDimension("android:radius")?.let {
                        appendLine("    layer.maxCornerRadius = $it")
                        appendLine("    layer.cornerRadius = $it")
                    } ?: run {
                        val radius = corners.attributeAsSwiftDimension("android:bottomLeftRadius")
                            ?: corners.attributeAsSwiftDimension("android:topLeftRadius")
                            ?: corners.attributeAsSwiftDimension("android:bottomRightRadius")
                            ?: corners.attributeAsSwiftDimension("android:topRightRadius")
                        if (radius != null) {
                            appendLine("    layer.cornerRadius = $radius")
                            appendLine("    if #available(iOS 11.0, *) {")
                            append("        layer.maskedCorners = [")
                            append(
                                mapOf(
                                    "android:bottomLeftRadius" to ".layerMinXMaxYCorner",
                                    "android:topLeftRadius" to ".layerMinXMinYCorner",
                                    "android:bottomRightRadius" to ".layerMaxXMaxYCorner",
                                    "android:topRightRadius" to ".layerMaxXMinYCorner"
                                ).filterKeys { corners.allAttributes.containsKey(it) }.values.joinToString()
                            )
                            appendLine("]")
                            appendLine("    }")
                        }
                    }
                }
                appendLine("    layer.bounds.size = CGSize(width: 100, height: 100)")
                appendLine("    return layer")
                appendLine("}")
            }
        }
    }
}

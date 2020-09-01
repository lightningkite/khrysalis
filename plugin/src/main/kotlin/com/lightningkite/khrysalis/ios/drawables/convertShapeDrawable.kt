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
                    appendln("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
                    appendln("    let mask = CAShapeLayer()")
                    appendln("    mask.path = CGPath(ellipseIn: CGRect(x: 0, y: 0, width: $width, height: $height), transform: nil)")
                    appendln("    let gradient = CAGradientLayer()")
                    appendln("    gradient.mask = mask")
                    val colors = listOfNotNull(
                        it.attributeAsSwiftColor("android:startColor"),
                        it.attributeAsSwiftColor("android:centerColor"),
                        it.attributeAsSwiftColor("android:endColor")
                    )
                    appendln("    gradient.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                    val angle = it.attributeAsInt("android:angle") ?: 0
                    appendln("    gradient.setGradientAngle(degrees: $angle)")
                    appendln("    gradient.onResize.subscribeBy { [weak mask] (rect) in ")
                    appendln("        mask?.path = CGPath(ellipseIn: rect, transform: nil)")
                    appendln("    }")
                    appendln("    return gradient")
                    appendln("}")
                } ?: run {
                    appendln("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
                    appendln("    let layer = CAShapeLayer()")
                    appendln("    layer.path = CGPath(ellipseIn: CGRect(x: 0, y: 0, width: $width, height: $height), transform: nil)")
                    node.children.find { it.name == "stroke" }?.let {
                        appendln("    layer.lineWidth = ${it.attributeAsSwiftDimension("android:width") ?: "0"}")
                        setToColor(it, "android:color") { it, s ->
                            appendln(
                                "    layer.strokeColor = $it.cgColor"
                            )
                        }
                    }
                    node.children.find { it.name == "solid" }?.let {
                        setToColor(it, "android:color") { it, s ->
                            appendln(
                                "    layer.fillColor = $it.cgColor"
                            )
                        }
                    } ?: run {
                        appendln("    layer.fillColor = nil")
                    }
                    appendln("    layer.onResize.subscribeBy { [weak layer] (rect) in ")
                    appendln("        layer?.path = CGPath(ellipseIn: rect, transform: nil)")
                    appendln("    }")
                    appendln("    return layer")
                    appendln("}")
                }
            }
        }
        else -> {
            val className = if (node.children.any { it.name == "gradient" }) "CAGradientLayer" else "CALayer"
            with(out) {
                appendln("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
                appendln("    let layer = $className()")
                node.children.find { it.name == "stroke" }?.let {
                    appendln("    layer.borderWidth = ${it.attributeAsSwiftDimension("android:width") ?: "0"}")
                    setToColor(it, "android:color") { it, s ->
                        appendln(
                            "    layer.borderColor = $it.cgColor"
                        )
                    }
                }
                node.children.find { it.name == "solid" }?.let {
                    setToColor(it, "android:color") { it, s ->
                        appendln(
                            "    layer.backgroundColor = $it.cgColor"
                        )
                    }
                } ?: node.children.find { it.name == "gradient" }?.let {
                    val colors = listOfNotNull(
                        it.attributeAsSwiftColor("android:startColor"),
                        it.attributeAsSwiftColor("android:centerColor"),
                        it.attributeAsSwiftColor("android:endColor")
                    )
                    appendln("    layer.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                    val angle = it.attributeAsInt("android:angle") ?: 0
                    appendln("    layer.setGradientAngle(degrees: $angle)")
                }
                node.children.find { it.name == "corners" }?.let { corners ->
                    corners.attributeAsSwiftDimension("android:radius")?.let {
                        appendln("    layer.maxCornerRadius = $it")
                        appendln("    layer.cornerRadius = $it")
                    } ?: run {
                        val radius = corners.attributeAsSwiftDimension("android:bottomLeftRadius")
                            ?: corners.attributeAsSwiftDimension("android:topLeftRadius")
                            ?: corners.attributeAsSwiftDimension("android:bottomRightRadius")
                            ?: corners.attributeAsSwiftDimension("android:topRightRadius")
                        if (radius != null) {
                            appendln("    layer.cornerRadius = $radius")
                            append("    layer.maskedCorners = [")
                            append(
                                mapOf(
                                    "android:bottomLeftRadius" to ".layerMinXMaxYCorner",
                                    "android:topLeftRadius" to ".layerMinXMinYCorner",
                                    "android:bottomRightRadius" to ".layerMaxXMaxYCorner",
                                    "android:topRightRadius" to ".layerMaxXMinYCorner"
                                ).filterKeys { corners.allAttributes.containsKey(it) }.values.joinToString()
                            )
                            appendln("]")
                        }
                    }
                }
                appendln("    layer.bounds.size = CGSize(width: 100, height: 100)")
                appendln("    return layer")
                appendln("}")
            }
        }
    }
}

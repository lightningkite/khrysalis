package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsColor
import com.lightningkite.kwift.utils.attributeAsDimension
import com.lightningkite.kwift.utils.attributeAsInt
import java.lang.Appendable


fun convertShapeDrawable(name: String, node: XmlNode, out: Appendable) {
    val className = if (node.children.any { it.name == "gradient" }) "CAGradientLayer" else "CALayer"
    with(out) {
        appendln("static func $name(_ view: UIView? = nil) -> $className {")
        appendln("    let layer = $className()")
        node.children.find { it.name == "stroke" }?.let {
            appendln("    layer.borderWidth = ${it.attributeAsDimension("android:width") ?: "0"}")
            appendln("    layer.borderColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor")
        }
        node.children.find { it.name == "solid" }?.let {
            appendln(
                "    layer.backgroundColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor"
            )
        }
        node.children.find { it.name == "corners" }?.let { corners ->
            corners.attributeAsDimension("android:radius")?.let {
                appendln("    layer.cornerRadius = $it")
            } ?: run {
                val radius = corners.attributeAsDimension("android:bottomLeftRadius")
                    ?: corners.attributeAsDimension("android:topLeftRadius")
                    ?: corners.attributeAsDimension("android:bottomRightRadius")
                    ?: corners.attributeAsDimension("android:topRightRadius")
                if(radius != null){
                    appendln("    layer.cornerRadius = $radius")
                    append("    layer.maskedCorners = [")
                    append(mapOf(
                        "android:bottomLeftRadius" to ".layerMinXMaxYCorner",
                        "android:topLeftRadius" to ".layerMinXMinYCorner",
                        "android:bottomRightRadius" to ".layerMaxXMaxYCorner",
                        "android:topRightRadius" to ".layerMaxXMinYCorner"
                    ).filterKeys { corners.attributes.containsKey(it) }.values.joinToString())
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

package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import java.lang.Appendable


fun convertSelectorDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing selector $name")
    with(out) {
        val after = ArrayList<() -> Unit>()

        appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
        appendLine("    let layer = CALayer()")
        node.children.forEachIndexed { index, subnode ->
            appendLine("    let part${index + 1}: CALayer = {")
            if (subnode.allAttributes.containsKey("android:drawable")) {
                appendLine(
                    "        let sublayer = ${
                        subnode.attributeAsSwiftLayer(
                            "android:drawable",
                            "view"
                        ) ?: "CALayer() /* Unknown */"
                    }"
                )
            } else {
                val subname = name + "Part" + (after.size + 1)
                after.add {
                    convertDrawableXml(subname, subnode.children.first(), out)
                }
                appendLine(
                    "        let sublayer = $subname.makeLayer(view)"
                )
            }
            subnode.attributeAsSwiftDimension("android:width")?.let {
                appendLine("        sublayer.frame.size.width = $it")
            }
            subnode.attributeAsSwiftDimension("android:height")?.let {
                appendLine("        sublayer.frame.size.height = $it")
            }
            appendLine("        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)")
            appendLine("        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in sublayer.resize(bounds) }")
            appendLine("        return sublayer")
            appendLine("    }()")
        }
        appendLine("    ")
        appendLine("    layer.addOnStateChange(view) { [unowned layer] state in ")
        appendLine("        layer.sublayers?.forEach { \$0.removeFromSuperlayer() }")
        node.children.forEachIndexed { index, subnode ->
            val conditions = ArrayList<String>()
            subnode.attributeAsBoolean("android:state_enabled")?.let {
                conditions += (if (it) "!" else "") + "state.contains(.disabled)"
            }
            subnode.attributeAsBoolean("android:state_pressed")?.let {
                conditions += (if (it) "" else "!") + "state.contains(.highlighted)"
            }
            subnode.attributeAsBoolean("android:state_selected")?.let {
                conditions += (if (it) "" else "!") + "state.contains(.selected)"
            }
            subnode.attributeAsBoolean("android:state_focused")?.let {
                conditions += (if (it) "" else "!") + "state.contains(.focused)"
            }
            subnode.attributeAsBoolean("android:state_checked")?.let {
                conditions += (if (it) "" else "!") + "state.contains(.selected)"
            }

            if (conditions.isEmpty()) {
                appendLine("        layer.addSublayer(part${index + 1})")
            } else {
                appendLine("        if ${conditions.joinToString(" && ")} {")
                appendLine("            layer.addSublayer(part${index + 1})")
                appendLine("            return")
                appendLine("        }")
            }
        }
        appendLine("    }")
        appendLine("    ")
        appendLine("    return layer")
        appendLine("}")
        after.forEach { it.invoke() }
    }
}

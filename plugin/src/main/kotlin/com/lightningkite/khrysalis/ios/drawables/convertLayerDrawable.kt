package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import java.lang.Appendable


fun convertLayerListDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing layer-list $name")
    with(out) {
        val after = ArrayList<() -> Unit>()

        appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
        appendLine("    let layer = CALayer()")
        for (subnode in node.children) {
            appendLine("    layer.addSublayer({")
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
                    appendLine("// writing $subname")
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
            appendLine("        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in ")
            appendLine("             var subBounds = bounds ")
            val paddingSubnode = subnode.children.firstOrNull()
                ?.children?.find { it.name == "padding" }
            (subnode.attributeAsSwiftDimension("android:top")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:top"))?.let {
                appendLine("             subBounds.origin.y += $it")
                appendLine("             subBounds.size.height -= $it")
            }
            (subnode.attributeAsSwiftDimension("android:left")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:left"))?.let {
                appendLine("             subBounds.origin.x += $it")
                appendLine("             subBounds.size.width -= $it")
            }
            (subnode.attributeAsSwiftDimension("android:right")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:right"))?.let {
                appendLine("             subBounds.size.width -= $it")
            }
            (subnode.attributeAsSwiftDimension("android:botton")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:botton"))?.let {
                appendLine("             subBounds.size.height -= $it")
            }
            appendLine("             sublayer.resize(subBounds) ")
            appendLine("        }")
            appendLine("        return sublayer")
            appendLine("    }())")
        }
        appendLine("    return layer")
        appendLine("}")
        after.forEach { it.invoke() }
    }
}

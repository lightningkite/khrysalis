package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import java.lang.Appendable


fun convertLayerListDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing layer-list $name")
    with(out) {
        val after = ArrayList<() -> Unit>()

        appendln("static func $name(_ view: UIView? = nil) -> CALayer {")
        appendln("    let layer = CALayer()")
        for (subnode in node.children) {
            appendln("    layer.addSublayer({")
            if (subnode.allAttributes.containsKey("android:drawable")) {
                appendln(
                    "        let sublayer = ${subnode.attributeAsSwiftLayer(
                        "android:drawable",
                        "view"
                    ) ?: "CALayer() /* Unknown */"}"
                )
            } else {
                val subname = name + "Part" + (after.size + 1)
                after.add {
                    appendln("// writing $subname")
                    convertDrawableXml(subname, subnode.children.first(), out)
                }
                appendln(
                    "        let sublayer = $subname(view)"
                )
            }
            subnode.attributeAsSwiftDimension("android:width")?.let {
                appendln("        sublayer.frame.size.width = $it")
            }
            subnode.attributeAsSwiftDimension("android:height")?.let {
                appendln("        sublayer.frame.size.height = $it")
            }
            appendln("        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)")
            appendln("        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in ")
            appendln("             var subBounds = bounds ")
            val paddingSubnode = subnode.children.firstOrNull()
                ?.children?.find { it.name == "padding" }
            (subnode.attributeAsSwiftDimension("android:top")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:top"))?.let {
                appendln("             subBounds.origin.y += $it")
                appendln("             subBounds.size.height -= $it")
            }
            (subnode.attributeAsSwiftDimension("android:left")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:left"))?.let {
                appendln("             subBounds.origin.x += $it")
                appendln("             subBounds.size.width -= $it")
            }
            (subnode.attributeAsSwiftDimension("android:right")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:right"))?.let {
                appendln("             subBounds.size.width -= $it")
            }
            (subnode.attributeAsSwiftDimension("android:botton")
                ?: paddingSubnode?.attributeAsSwiftDimension("android:botton"))?.let {
                appendln("             subBounds.size.height -= $it")
            }
            appendln("             sublayer.resize(subBounds) ")
            appendln("        }")
            appendln("        return sublayer")
            appendln("    }())")
        }
        appendln("    return layer")
        appendln("}")
        after.forEach { it.invoke() }
    }
}

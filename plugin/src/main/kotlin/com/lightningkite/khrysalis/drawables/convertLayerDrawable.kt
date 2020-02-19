package com.lightningkite.khrysalis.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsDimension
import com.lightningkite.khrysalis.utils.attributeAsLayer
import java.lang.Appendable


fun convertLayerListDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing layer-list $name")
    with(out) {
        val after = ArrayList<()->Unit>()

        appendln("static func $name(_ view: UIView? = nil) -> CALayer {")
        appendln("    let layer = CALayer()")
        for (subnode in node.children) {
            appendln("    layer.addSublayer({")
            if(subnode.attributes.containsKey("android:drawable")){
                appendln(
                    "        let sublayer = ${subnode.attributeAsLayer("android:drawable", "view") ?: "CALayer() /* Unknown */"}"
                )
            } else {
                val subname = name + "Part" + (after.size + 1)
                after.add {
                    convertDrawableXml(subname, subnode.children.first(), out)
                }
                appendln(
                    "        let sublayer = $subname(view)"
                )
            }
            subnode.attributeAsDimension("android:width")?.let {
                appendln("        sublayer.frame.size.width = $it")
            }
            subnode.attributeAsDimension("android:height")?.let {
                appendln("        sublayer.frame.size.height = $it")
            }
            appendln("        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)")
            appendln("        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in ")
            appendln("             var subBounds = bounds ")
            subnode.attributeAsDimension("android:top")?.let {
                appendln("             subBounds.origin.y += $it")
                appendln("             subBounds.size.height -= $it")
            }
            subnode.attributeAsDimension("android:left")?.let {
                appendln("             subBounds.origin.x += $it")
                appendln("             subBounds.size.width -= $it")
            }
            subnode.attributeAsDimension("android:right")?.let {
                appendln("             subBounds.size.width -= $it")
            }
            subnode.attributeAsDimension("android:botton")?.let {
                appendln("             subBounds.size.height -= $it")
            }
            appendln("             sublayer.frame = subBounds ")
            appendln("        }")
            appendln("        return sublayer")
            appendln("    }())")
        }
        appendln("    return layer")
        appendln("}")
        after.forEach { it.invoke() }
    }
}

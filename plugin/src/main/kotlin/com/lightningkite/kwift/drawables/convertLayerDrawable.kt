package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsDimension
import com.lightningkite.kwift.utils.attributeAsLayer
import java.lang.Appendable


fun convertLayerListDrawable(name: String, node: XmlNode, out: Appendable) {
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
            appendln("        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in sublayer.frame = bounds }")
            appendln("        return sublayer")
            appendln("    }())")
        }
        appendln("    return layer")
        appendln("}")
        after.forEach { it.invoke() }
    }
}

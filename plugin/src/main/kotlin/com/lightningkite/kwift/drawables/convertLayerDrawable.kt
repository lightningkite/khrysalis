package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsDimension
import com.lightningkite.kwift.utils.attributeAsLayer
import java.lang.Appendable


fun convertLayerListDrawable(name: String, node: XmlNode, out: Appendable) {
    with(out) {
        val after = ArrayList<()->Unit>()

        appendln("static func $name(view: UIView? = nil) -> CALayer {")
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
                    "        let sublayer = $subname(view: view)"
                )
            }
            subnode.attributeAsDimension("android:width")?.let {
                appendln("        sublayer.frame.size.width = $it")
                appendln("        sublayer.matchSize(nil)")
            }
            subnode.attributeAsDimension("android:height")?.let {
                appendln("        sublayer.frame.size.height = $it")
                appendln("        sublayer.matchSize(nil)")
            }
            appendln("        return sublayer")
            appendln("    }())")
        }
        appendln("    layer.matchSize(view)")
        appendln("    return layer")
        appendln("}")
        after.forEach { it.invoke() }
    }
}

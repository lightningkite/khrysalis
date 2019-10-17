package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.*
import java.lang.Appendable


fun convertBitmapDrawable(name: String, node: XmlNode, out: Appendable) {
    with(out) {
        appendln("static func $name(view: UIView? = nil) -> CALayer {")
        appendln("    let layer = CALayer()")
        appendln("    layer.backgroundColor = ${node.attributeAsColor("android:tint")}.cgColor")
        appendln("    let mask = ${node.attributeAsLayer("android:src", "view")}")
        appendln("    layer.mask = mask")
        appendln("    layer.bounds.size = mask.bounds.size")
        appendln("    layer.onResize.addAndRunWeak(mask, layer.bounds) { (mask, bounds) in mask.frame = bounds }")
        appendln("    return layer")
        appendln("}")
    }
}

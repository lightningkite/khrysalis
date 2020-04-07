package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
import java.lang.Appendable


fun convertBitmapDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing bitmap $name")
    with(out) {
        appendln("static func $name(_ view: UIView? = nil) -> CALayer {")
        appendln("    let layer = CALayer()")
        appendln("    layer.backgroundColor = ${node.attributeAsColor("android:tint")}.cgColor")
        appendln("    let mask = ${node.attributeAsLayer("android:src", "view")}")
        appendln("    layer.mask = mask")
        appendln("    layer.bounds.size = mask.bounds.size")
        appendln("    layer.onResize.startWith(layer.bounds).addWeak(mask) { (mask, bounds) in mask.frame = bounds }")
        appendln("    return layer")
        appendln("}")
    }
}

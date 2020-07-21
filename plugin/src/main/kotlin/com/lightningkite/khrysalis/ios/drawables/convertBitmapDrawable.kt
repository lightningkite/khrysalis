package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import com.lightningkite.khrysalis.ios.layout.setToColor
import java.lang.Appendable


fun convertBitmapDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing bitmap $name")
    with(out) {
        appendln("static func $name(_ view: UIView? = nil) -> CALayer {")
        appendln("    let layer = CALayer()")
        setToColor(node, "android:tint"){ it, s ->
            appendln("    layer.backgroundColor = $it.cgColor")
        }
        appendln("    let mask = ${node.attributeAsSwiftLayer("android:src", "view")}")
        appendln("    layer.mask = mask")
        appendln("    layer.bounds.size = mask.bounds.size")
        appendln("    layer.onResize.startWith(layer.bounds).addWeak(mask) { (mask, bounds) in mask.frame = bounds }")
        appendln("    return layer")
        appendln("}")
    }
}

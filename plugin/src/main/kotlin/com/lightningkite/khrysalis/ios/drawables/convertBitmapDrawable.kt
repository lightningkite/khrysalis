package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import com.lightningkite.khrysalis.ios.layout.setToColor
import java.lang.Appendable


fun convertBitmapDrawable(name: String, node: XmlNode, out: Appendable) {
    println("Writing bitmap $name")
    with(out) {
        appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in ")
        appendLine("    let layer = CALayer()")
        setToColor(node, "android:tint") { it, s ->
            appendLine("    layer.backgroundColor = $it.cgColor")
        }
        appendLine("    let mask = ${node.attributeAsSwiftLayer("android:src", "view")}")
        appendLine("    layer.mask = mask")
        appendLine("    layer.bounds.size = mask.bounds.size")
        appendLine("    layer.onResize.startWith(layer.bounds).addWeak(mask) { (mask, bounds) in mask.frame = bounds }")
        appendLine("    return layer")
        appendLine("}")
    }
}

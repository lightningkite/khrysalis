package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.*
import java.lang.Appendable

fun <E> MutableList<E>.unshift(): E {
    return removeAt(0)
}

val pathLetters = charArrayOf(
    'M',
    'L',
    'Z',
    'H',
    'V',
    'Q',
    'T',
    'C',
    'S',
    'A'
)
val spaceOrComma = Regex("[ ,]+")
fun convertVectorDrawable(name: String, node: XmlNode, out: Appendable) {

    val scaleX = (node.attributeAsDimension("android:width")?.toDouble() ?: 10.0) / (node.attributeAsDouble("android:viewportWidth")?.toDouble() ?: 10.0)
    val scaleY = (node.attributeAsDimension("android:height")?.toDouble() ?: 10.0) / (node.attributeAsDouble("android:viewportHeight")?.toDouble() ?: 10.0)
    fun Double.scaleX(): Double = this * scaleX
    fun Double.scaleY(): Double = this * scaleY

    with(out) {
        appendln("static func $name(view: UIView? = nil) -> CALayer {")
        appendln("    let layer = CALayer()")
        node.children.filter { it.name == "path" }.forEach { subnode ->
            appendln("    layer.addSublayer({")
            appendln("        let sublayer = CAShapeLayer()")
            subnode.attributes["android:pathData"]?.let { pathData ->
                appendln("        let path = CGMutablePath()")
                var referenceX: Double = 0.0
                var referenceY: Double = 0.0
                var previousC2X: Double = 0.0
                var previousC2Y: Double = 0.0
                var stringIndex = pathData.indexOfAny(pathLetters, 0, true)
                while (true) {
                    var nextLetterIndex = pathData.indexOfAny(pathLetters, stringIndex + 1, true)
                    if (nextLetterIndex == -1) nextLetterIndex = pathData.length

                    val rawInstruction: Char = pathData[stringIndex]
                    val arguments: MutableList<Double> = pathData
                        .substring(stringIndex + 1, nextLetterIndex)
                        .split(spaceOrComma)
                        .mapNotNull { it.toDoubleOrNull() }
                        .toMutableList()

                    val instruction = rawInstruction.toLowerCase()
                    val isAbsolute: Boolean = rawInstruction.isUpperCase()
                    fun offsetX(): Double = if(isAbsolute) 0.0 else referenceX
                    fun offsetY(): Double = if(isAbsolute) 0.0 else referenceY
                    var updateReference = true
                    appendln("        //$rawInstruction ${arguments.joinToString()}")
                    while (arguments.isNotEmpty()) {
                        when (instruction) {
                            'm' -> {
                                val destX = arguments.unshift() + offsetX()
                                val destY = arguments.unshift() + offsetY()
                                referenceX = destX
                                referenceY = destY
                                appendln("        path.move(to: CGPoint(x: ${destX.scaleX()}, y: ${destY.scaleY()}))")
                            }
                            'l' -> {
                                val destX = arguments.unshift() + offsetX()
                                val destY = arguments.unshift() + offsetY()
                                referenceX = destX
                                referenceY = destY
                                appendln("        path.addLine(to: CGPoint(x: ${destX.scaleX()}, y: ${destY.scaleY()}))")
                            }
                            'z' -> {
                                appendln("        path.closeSubpath()")
                            }
                            'h' -> {
                                updateReference = false
                                referenceX =
                                    if (isAbsolute) arguments.unshift() else referenceX + arguments.unshift()
                                appendln("        path.addLine(to: CGPoint(x: ${referenceX.scaleX()}, y: ${referenceY.scaleY()}))")
                            }
                            'v' -> {
                                updateReference = false
                                referenceY =
                                    if (isAbsolute) arguments.unshift() else referenceY + arguments.unshift()
                                appendln("        path.addLine(to: CGPoint(x: ${referenceX.scaleX()}, y: ${referenceY.scaleY()}))")
                            }
                            'q' -> {
                                val controlX = arguments.unshift() + offsetX()
                                val controlY = arguments.unshift() + offsetY()
                                val destX = arguments.unshift() + offsetX()
                                val destY = arguments.unshift() + offsetY()
                                previousC2X = controlX
                                previousC2Y = controlY
                                referenceX = destX
                                referenceY = destY
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${destX.scaleX()}, y: ${destY.scaleY()}), control: CGPoint(x: ${controlX.scaleX()}, y: ${controlY.scaleY()}))")
                            }
                            't' -> {
                                val destX = arguments.unshift() + offsetX()
                                val destY = arguments.unshift() + offsetY()
                                val controlX = referenceX - (referenceX - previousC2X)
                                val controlY = referenceY - (referenceY - previousC2Y)
                                referenceX = destX
                                referenceY = destY
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${destX.scaleX()}, y: ${destY.scaleY()}), control: CGPoint(x: ${controlX.scaleX()}, y: ${controlY.scaleY()}))")
                            }
                            'c' -> {
                                val control1X = arguments.unshift() + offsetX()
                                val control1Y = arguments.unshift() + offsetY()
                                val control2X = arguments.unshift() + offsetX()
                                val control2Y = arguments.unshift() + offsetY()
                                val destX = arguments.unshift() + offsetX()
                                val destY = arguments.unshift() + offsetY()
                                previousC2X = control2X
                                previousC2Y = control2Y
                                referenceX = destX
                                referenceY = destY
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${destX.scaleX()}, y: ${destY.scaleY()}), control1: CGPoint(x: ${control1X.scaleX()}, y: ${control1Y.scaleY()}), control2: CGPoint(x: ${control2X.scaleX()}, y: ${control2Y.scaleY()}))")
                            }
                            's' -> {
                                val control2X = arguments.unshift() + offsetX()
                                val control2Y = arguments.unshift() + offsetY()
                                val destX = arguments.unshift() + offsetX()
                                val destY = arguments.unshift() + offsetY()
                                val c1x = referenceX - (referenceX - previousC2X)
                                val c1y = referenceY - (referenceY - previousC2Y)
                                referenceX = destX
                                referenceY = destY
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${destX.scaleX()}, y: ${destY.scaleY()}), control1: CGPoint(x: ${c1x.scaleX()}, y: ${c1y.scaleY()}), control2: CGPoint(x: ${control2X.scaleX()}, y: ${control2Y.scaleY()}))")
                            }
                            'a' -> {
                                appendln("        //TODO - support arcs")
                            }
                        }
                    }

                    stringIndex = nextLetterIndex
                    if (nextLetterIndex == pathData.length) break
                }
                appendln("        sublayer.path = path")
            }
            subnode.attributeAsColor("android:fillColor")?.let {
                appendln("        sublayer.fillColor = $it.cgColor")
            }
            subnode.attributeAsColor("android:strokeColor")?.let {
                appendln("        sublayer.strokeColor = $it.cgColor")
            }
            appendln("        return sublayer")
            appendln("    }())")
        }
        val width = node.attributeAsDimension("android:width") ?: 0
        val height = node.attributeAsDimension("android:height") ?: 0
        appendln("    layer.bounds.size = CGSize(width: ${width}, height: ${height})")
        appendln("    return layer")
        appendln("}")
    }

}

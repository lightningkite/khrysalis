package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsColor
import com.lightningkite.kwift.utils.attributeAsDimension
import com.lightningkite.kwift.utils.attributeAsString
import java.io.File


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
fun convertVectorDrawable(name: String, node: XmlNode, destination: File) {
    destination.writeText(
        buildString {
            appendln("//Automatically created by Kwift")
            appendln("import UIKit")
            appendln("")
            appendln("func $name() -> CALayer {")
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
                        if(nextLetterIndex == -1) nextLetterIndex = pathData.length

                        val rawInstruction: Char = pathData[stringIndex]
                        val rawArguments: List<Double> = pathData
                            .substring(stringIndex + 1, nextLetterIndex)
                            .split(spaceOrComma)
                            .mapNotNull { it.toDoubleOrNull() }

                        val instruction = rawInstruction.toLowerCase()
                        val isAbsolute: Boolean = rawInstruction.isUpperCase()
                        val arguments = if (isAbsolute) rawArguments else rawArguments.mapIndexed { index, it ->
                            it + if (index % 2 == 0) referenceX else referenceY
                        }
                        var updateReference = true
                        when (instruction) {
                            'm' -> appendln("        path.move(to: CGPoint(x: ${arguments[0]}, y: ${arguments[1]}))")
                            'l' -> appendln("        path.addLine(to: CGPoint(x: ${arguments[0]}, y: ${arguments[1]}))")
                            'z' -> appendln("        path.closeSubpath()")
                            'h' -> {
                                updateReference = false
                                referenceX = if (isAbsolute) rawArguments[0] else referenceX + rawArguments[0]
                                appendln("        path.addLine(to: CGPoint(x: ${referenceX}, y: ${referenceY}))")
                            }
                            'v' -> {
                                updateReference = false
                                referenceY = if (isAbsolute) rawArguments[0] else referenceY + rawArguments[0]
                                appendln("        path.addLine(to: CGPoint(x: ${referenceX}, y: ${referenceY}))")
                            }
                            'q' -> {
                                previousC2X = arguments[0]
                                previousC2Y = arguments[1]
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${arguments[2]}, y: ${arguments[3]}), control: CGPoint(x: ${arguments[0]}, y: ${arguments[1]}))")
                            }
                            't' -> {
                                val c1x = referenceX - (referenceX - previousC2X)
                                val c1y = referenceY - (referenceY - previousC2Y)
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${arguments[0]}, y: ${arguments[1]}), control: CGPoint(x: ${c1x}, y: ${c1y}))")
                            }
                            'c' -> {
                                previousC2X = arguments[2]
                                previousC2Y = arguments[3]
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${arguments[4]}, y: ${arguments[5]}), control1: CGPoint(x: ${arguments[0]}, y: ${arguments[1]}), control2: CGPoint(x: ${arguments[2]}, y: ${arguments[3]}))")
                            }
                            's' -> {
                                val c1x = referenceX - (referenceX - previousC2X)
                                val c1y = referenceY - (referenceY - previousC2Y)
                                appendln("        path.addQuadCurve(to: CGPoint(x: ${arguments[2]}, y: ${arguments[3]}), control1: CGPoint(x: ${c1x}, y: ${c1y}), control2: CGPoint(x: ${arguments[0]}, y: ${arguments[1]}))")
                            }
                            'a' -> {
                                appendln("        //TODO - support arcs")
                            }
                        }
                        if (updateReference) {
                            referenceX = arguments.getOrNull(arguments.lastIndex - 1) ?: referenceX
                            referenceY = arguments.getOrNull(arguments.lastIndex) ?: referenceY
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
            appendln("    return layer")
            appendln("}")
        }
    )
}

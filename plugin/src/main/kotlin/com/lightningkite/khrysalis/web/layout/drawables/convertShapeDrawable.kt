package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.asCssDimension
import com.lightningkite.khrysalis.web.attributeAsCssColor
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.io.File
import java.lang.Appendable


fun convertShapeDrawable(webDrawablesFolder: File, currentDrawable: String, selectors: String, node: XmlNode, out: Appendable) {
    out.appendln("$selectors {")
    when (node.allAttributes["android:shape"]) {
        "oval" -> {
            node.children.find { it.name == "gradient" }?.let {
                val colors = listOfNotNull(
                    it.attributeAsCssColor("android:startColor"),
                    it.attributeAsCssColor("android:centerColor"),
                    it.attributeAsCssColor("android:endColor")
                )
                val angle = it.attributeAsInt("android:angle")?.toInt() ?: 0
                out.appendln("background-image: linear-gradient(${angle-90}deg, ${colors.joinToString()})")
            } ?: run {
                node.children.find { it.name == "solid" }?.let {
                    val color = it.attributeAsCssColor("android:color")
                    out.appendln("background-color: $color;")
                }
            }
            node.children.find { it.name == "stroke" }?.let {
                val color = it.attributeAsCssColor("android:color")
                out.appendln("border-color: $color;")
                val width = it.directAttributes["android:width"]?.asCssDimension()
                out.appendln("border-width: $width;")
                out.appendln("border-style: solid;")
            }
            out.appendln("border-radius: 50%;")
        }
        "rectangle" -> {
            node.children.find { it.name == "gradient" }?.let {
                val colors = listOfNotNull(
                    it.attributeAsCssColor("android:startColor"),
                    it.attributeAsCssColor("android:centerColor"),
                    it.attributeAsCssColor("android:endColor")
                )
                val angle = it.attributeAsInt("android:angle")?.toInt() ?: 0
                out.appendln("background-image: linear-gradient(${angle-90}deg, ${colors.joinToString()})")
            } ?: run {
                node.children.find { it.name == "solid" }?.let {
                    val color = it.attributeAsCssColor("android:color")
                    out.appendln("background-color: $color;")
                }
            }
            node.children.find { it.name == "stroke" }?.let {
                val color = it.attributeAsCssColor("android:color")
                out.appendln("border-color: $color;")
                val width = it.directAttributes["android:width"]?.asCssDimension()
                out.appendln("border-width: $width;")
                out.appendln("border-style: solid;")
            }

            node.children.find { it.name == "corners" }?.let { corners ->
                corners.directAttributes["android:radius"]?.asCssDimension()?.let {
                    out.appendln("border-radius: $it;")
                } ?: run {
                    corners.directAttributes["android:topLeftRadius"]?.asCssDimension()?.let {
                        out.appendln("border-top-left-radius: $it;")
                    }
                    corners.directAttributes["android:topRightRadius"]?.asCssDimension()?.let {
                        out.appendln("border-top-right-radius: $it;")
                    }
                    corners.directAttributes["android:bottomRightRadius"]?.asCssDimension()?.let {
                        out.appendln("border-bottom-right-radius: $it;")
                    }
                    corners.directAttributes["android:bottomLeftRadius"]?.asCssDimension()?.let {
                        out.appendln("border-bottom-left-radius: $it;")
                    }
                    out.appendln(";")
                }
            }
        }
    }
    out.appendln("}")
}

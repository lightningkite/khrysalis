package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsDouble
import com.lightningkite.khrysalis.web.attributeAsCssColor
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.io.File
import java.net.URLEncoder

fun convertVectorDrawable(
    webDrawablesFolder: File,
    currentDrawable: String,
    selectors: String,
    node: XmlNode,
    out: Appendable
) {
    val file = webDrawablesFolder.resolve("$currentDrawable.svg")
    file.printWriter().use { svgOut ->
        val width = node.attributeAsDouble("android:width") ?: 0.0
        val height = node.attributeAsDouble("android:height") ?: 0.0
        val viewportWidth = node.attributeAsDouble("android:viewportWidth") ?: 0.0
        val viewportHeight = node.attributeAsDouble("android:viewportHeight") ?: 0.0
        svgOut.appendln("<svg xmlns='http://www.w3.org/2000/svg' width='${width}' height='${height}' viewBox='0 0 $viewportWidth $viewportHeight'>")
        node.children
            .filter { it.name == "path" }
            .withIndex()
            .mapNotNull { (index, subnode) ->
                subnode.children
                    .find { it.name == "aapt:attr" && it.allAttributes["name"] == "android:fillColor" }
                    ?.children?.find { it.name == "gradient" }?.let { index to it }
            }
            .takeUnless { it.isEmpty() }
            ?.let {
                svgOut.appendln("<defs>")
                it.forEach { (index, gradientNode) ->
                    val x1 = gradientNode.attributeAsDouble("android:startX")
                    val y1 = gradientNode.attributeAsDouble("android:startY")
                    val x2 = gradientNode.attributeAsDouble("android:endX")
                    val y2 = gradientNode.attributeAsDouble("android:endY")
                    svgOut.appendln("<linearGradient id='grad$index' x1='$x1' y1='$y1' x2='$x2' y2='$y2'>")
                    gradientNode.children.filter { it.name == "item" }.forEach {
                        val color = it.attributeAsCssColor("android:color")
                        val offset = (it.attributeAsDouble("android:offset") ?: 0.0) * 100
                        svgOut.appendln("<stop offset='$offset%' style='stop-color: $color;'/>")
                    }
                    gradientNode.attributeAsCssColor("android:startColor")?.let {
                        svgOut.appendln("<stop offset='0%' style='stop-color: $it;'/>")
                    }
                    gradientNode.attributeAsCssColor("android:endColor")?.let {
                        svgOut.appendln("<stop offset='0%' style='stop-color: $it;'/>")
                    }
                    svgOut.appendln("</linearGradient>")
                }
                svgOut.appendln("</defs>")
            }
        node.children.filter { it.name == "path" }.forEachIndexed { index, subnode ->
            subnode.children
                .find { it.name == "aapt:attr" && it.allAttributes["name"] == "android:fillColor" }
                ?.children?.find { it.name == "gradient" }
                ?.let { gradientNode ->
                    svgOut.appendln("<path d='${subnode.directAttributes["android:pathData"]}' fill='url(#grad${index})'/>")
                } ?: run {

                svgOut.appendln(
                    "<path d='${subnode.directAttributes["android:pathData"]}' fill='${subnode.attributeAsCssColor(
                        "android:fillColor"
                    )}'/>"
                )
            }
        }
        svgOut.appendln("</svg>")
    }

    out.appendln("$selectors {")
    out.appendln("background-image: url(\"images/${file.name}\")")
    out.appendln("}")
}

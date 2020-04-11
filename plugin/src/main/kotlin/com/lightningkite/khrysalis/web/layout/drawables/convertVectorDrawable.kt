package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsDouble
import com.lightningkite.khrysalis.web.attributeAsCssColor
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.net.URLEncoder

fun convertVectorDrawable(
    selectors: String,
    node: XmlNode,
    out: Appendable
) {
    out.appendln("$selectors {")
    out.append("background-image: url(data:image/svg+xml;utf8,")
    out.append(buildString {
        val width = node.attributeAsDouble("android:width") ?: 0.0
        val height = node.attributeAsDouble("android:height") ?: 0.0
        val viewportWidth = node.attributeAsDouble("android:viewportWidth") ?: 0.0
        val viewportHeight = node.attributeAsDouble("android:viewportHeight") ?: 0.0
        appendln("<svg xmlns='http://www.w3.org/2000/svg' width='${width}' height='${height}' viewBox='0 0 $viewportWidth $viewportHeight'>")
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
                appendln("<defs>")
                it.forEach { (index, gradientNode) ->
                    val x1 = gradientNode.attributeAsDouble("android:startX")
                    val y1 = gradientNode.attributeAsDouble("android:startY")
                    val x2 = gradientNode.attributeAsDouble("android:endX")
                    val y2 = gradientNode.attributeAsDouble("android:endY")
                    appendln("<linearGradient id='grad$index' x1='$x1' y1='$y1' x2='$x2' y2='$y2'>")
                    gradientNode.children.filter { it.name == "item" }.forEach {
                        val color = it.attributeAsCssColor("android:color")
                        val offset = (it.attributeAsDouble("android:offset") ?: 0.0) * 100
                        appendln("<stop offset='$offset%' style='stop-color: $color;'/>")
                    }
                    gradientNode.attributeAsCssColor("android:startColor")?.let {
                        appendln("<stop offset='0%' style='stop-color: $it;'/>")
                    }
                    gradientNode.attributeAsCssColor("android:endColor")?.let {
                        appendln("<stop offset='0%' style='stop-color: $it;'/>")
                    }
                    appendln("</linearGradient>")
                }
                appendln("</defs>")
            }
        node.children.filter { it.name == "path" }.forEachIndexed { index, subnode ->
            subnode.children
                .find { it.name == "aapt:attr" && it.allAttributes["name"] == "android:fillColor" }
                ?.children?.find { it.name == "gradient" }
                ?.let { gradientNode ->
                    appendln("<path d='${subnode.directAttributes["android:pathData"]}' fill='url(#grad${index})'/>")
                } ?: run {

                appendln(
                    "<path d='${subnode.directAttributes["android:pathData"]}' fill='${subnode.attributeAsCssColor(
                        "android:fillColor"
                    )}'/>"
                )
            }
        }
        appendln("</svg>")
    }.let { URLEncoder.encode(it, "UTF-8") })
    out.appendln(");")
    out.appendln("}")
}

package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsDouble
import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.attributeAsCssColor
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import com.lightningkite.khrysalis.web.layout.WebResources
import com.lightningkite.khrysalis.web.useScssVariables
import java.io.File
import java.net.URLEncoder

fun convertVectorDrawable(
    webDrawablesFolder: File,
    currentDrawable: String,
    selectors: String,
    node: XmlNode,
    out: Appendable,
    resources: WebResources
) {
    val colorResolver: String.()->String = label@{
        val value = this
        return@label when {
            value.startsWith("@") -> {
                val name = value.substringAfter('/')
                resources.colors[name]?.rawValue ?: "#FFF"
            }
            value.startsWith("#") -> {
                when (value.length - 1) {
                    3 -> "#" + value[1].toString().repeat(2) + value[2].toString().repeat(2) + value[3].toString()
                        .repeat(2)
                    4 -> "#" + value[2].toString().repeat(2) + value[3].toString().repeat(2) + value[4].toString()
                        .repeat(2) + value[1].toString().repeat(2)
                    6 -> value
                    8 -> "#" + value.drop(3).take(6) + value.drop(1).take(2)
                    else -> "#000000"
                }
            }
            else -> "black"
        }
    }

    val file = webDrawablesFolder.resolve("$currentDrawable.svg")
    file.printWriter().use { svgOut ->
        androidVectorToSvg(node, colorResolver, svgOut)
    }

    out.appendln("$selectors {")
    out.appendln("background-image: url(\"./images/${file.name}\")")
    out.appendln("}")
}

fun androidVectorToSvg(
    node: XmlNode,
    colorResolver: (String) -> String,
    svgOut: Appendable
) {
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
                    val color = it.allAttributes.get("android:color")
                    val offset = (it.attributeAsDouble("android:offset") ?: 0.0) * 100
                    svgOut.appendln("<stop offset='$offset%' style='stop-color: $color;'/>")
                }
                gradientNode.allAttributes.get("android:startColor")?.let(colorResolver)?.let {
                    svgOut.appendln("<stop offset='0%' style='stop-color: $it;'/>")
                }
                gradientNode.allAttributes.get("android:endColor")?.let(colorResolver)?.let {
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

            svgOut.appendln("<path d='${subnode.directAttributes["android:pathData"]}' ")
            subnode.allAttributes.get(
                "android:fillColor"
            )?.let(colorResolver)?.let {
                svgOut.appendln("fill='$it'")
            } ?: run {
                svgOut.appendln("fill='none'")
            }
            subnode.allAttributes.get(
                "android:strokeColor"
            )?.let(colorResolver)?.let {
                svgOut.appendln("stroke='$it'")
            }
            subnode.attributeAsDouble(
                "android:strokeWidth"
            )?.let {
                svgOut.appendln("stroke-width='$it'")
            }
            svgOut.appendln("/>")
        }
    }
    svgOut.appendln("</svg>")
}
package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.io.File
import java.lang.Appendable
import java.lang.Exception


fun convertDrawableXmls(
    css: Appendable,
    androidResourcesFolder: File,
    webDrawablesFolder: File
) {
    androidResourcesFolder.listFiles()!!
        .asSequence()
        .filter { it.name.startsWith("drawable") && it.isDirectory }
        .sortedByDescending {
            it.name.substringAfter('-').filter { it.isDigit() }.takeUnless { it.isEmpty() }?.toInt() ?: 0
        }
        .flatMap { it.walkTopDown().filter { it.extension == "xml" } }
        .distinctBy { it.name }
        .sortedBy { it.name }
        .map { it to XmlNode.read(it, mapOf()) }
        .forEach { (file, it) ->
            try {
                val cssName = file.nameWithoutExtension.kabobCase()
                css.appendln("/* ${cssName} */")
                css.append(buildString {
                    convertDrawableXml(".drawable-${cssName}", it, this)
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}

fun convertDrawableXml(
    selectors: String,
    node: XmlNode,
    out: Appendable
) {
    when (node.name.toLowerCase()) {
        "selector" -> convertSelectorDrawable(
            selectors,
            node,
            out
        )
        "shape" -> convertShapeDrawable(
            selectors,
            node,
            out
        )
        "layer-list" -> convertLayerListDrawable(
            selectors,
            node,
            out
        )
        "vector" -> convertVectorDrawable(
            selectors,
            node,
            out
        )
        "bitmap" -> convertBitmapDrawable(
            selectors,
            node,
            out
        )
    }
}


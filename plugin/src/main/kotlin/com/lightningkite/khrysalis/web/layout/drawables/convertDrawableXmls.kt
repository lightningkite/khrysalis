package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import com.lightningkite.khrysalis.web.layout.WebResources
import java.io.File
import java.lang.Appendable
import java.lang.Exception


fun convertDrawableXmls(
    css: Appendable,
    androidResourcesFolder: File,
    webDrawablesFolder: File,
    resources: WebResources
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
                val originalName = file.nameWithoutExtension
                val cssName = originalName.kabobCase()
                if(it.name.toLowerCase() == "vector") {
                    val svgFile = webDrawablesFolder.resolve("$cssName.svg")
                    resources.drawables[file.nameWithoutExtension] = WebResources.Drawable(cssName, "images/${svgFile.name}")
                } else {
                    resources.drawables[file.nameWithoutExtension] = WebResources.Drawable(cssName)
                }

                css.appendln("/* ${cssName} */")
                css.append(buildString {
                    convertDrawableXml(webDrawablesFolder, cssName, ".drawable-${cssName}", it, this, resources)
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}

fun convertDrawableXml(
    webDrawablesFolder: File,
    currentDrawable: String,
    selectors: String,
    node: XmlNode,
    out: Appendable,
    resources: WebResources
) {
    when (node.name.toLowerCase()) {
        "selector" -> convertSelectorDrawable(
            webDrawablesFolder,
            currentDrawable,
            selectors,
            node,
            out,
            resources
        )
        "shape" -> convertShapeDrawable(
            webDrawablesFolder,
            currentDrawable,
            selectors,
            node,
            out
        )
        "layer-list" -> convertLayerListDrawable(
            webDrawablesFolder,
            currentDrawable,
            selectors,
            node,
            out
        )
        "vector" -> convertVectorDrawable(
            webDrawablesFolder,
            currentDrawable,
            selectors,
            node,
            out,
            resources
        )
        "bitmap" -> convertBitmapDrawable(
            webDrawablesFolder,
            currentDrawable,
            selectors,
            node,
            out
        )
    }
}


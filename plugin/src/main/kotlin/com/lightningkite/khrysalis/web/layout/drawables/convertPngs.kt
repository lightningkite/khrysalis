package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import com.lightningkite.khrysalis.web.layout.WebResources
import java.io.File
import java.lang.Appendable
import kotlin.math.absoluteValue

private val dpis = listOf(
    120,
    160,
    240,
    320,
    360,
    400
)

fun convertPngs(
    css: Appendable,
    androidResourcesFolder: File,
    webDrawablesFolder: File,
    resources: WebResources
) {
    val pngNames = (androidResourcesFolder.listFiles() ?: arrayOf())
        .asSequence()
        .filter { it.name.startsWith("drawable") }
        .flatMap { it.walkTopDown() }
        .filter { it.extension == "png" }
        .map { it.nameWithoutExtension }
        .distinct()
        .toList()
        .sortedBy { it }


    pngNames.forEach { pngName ->
        val matching = (androidResourcesFolder.listFiles() ?: arrayOf())
            .asSequence()
            .filter { it.name.startsWith("drawable") }
            .flatMap { it.walkTopDown() }
            .filter { it.name == pngName + ".png" }.associateBy {
                it.parentFile.name.let { name ->
                    when {
                        name.contains("ldpi") -> 0
                        name.contains("mdpi") -> 1
                        name.contains("hdpi") -> 2
                        name.contains("xhdpi") -> 3
                        name.contains("xxhdpi") -> 4
                        name.contains("xxxhdpi") -> 5
                        else -> 2
                    }
                }
            }
            .entries
            .sortedBy { it.key }

        css.append(buildString {
            val cssName = pngName.kabobCase()
            appendln("/* ${cssName} */")
            matching.minBy { (it.key - 2).absoluteValue }?.let { (key, file) ->
                val destFile = webDrawablesFolder.resolve(file.nameWithoutExtension.kabobCase() + ".png")
                file.copyTo(destFile)
                resources.drawables[pngName] = WebResources.Drawable(cssName, "images/${destFile.name}")
                appendln(".drawable-${cssName} {")
                appendln("background-image: url(\"images/${destFile.name}\");")
                appendln("}")
            }
        })
    }
}

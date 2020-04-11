package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.io.File
import java.lang.Appendable

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
    webDrawablesFolder: File
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
            matching.forEachIndexed { index, (size, file) ->
                val destFile = webDrawablesFolder.resolve(file.nameWithoutExtension + "-" + size + ".png")
                file.copyTo(destFile)
                fun writeImageCss() {
                    appendln(".drawable-${cssName} {")
                    appendln("background-image: url(\"images/${destFile.name}\");")
                    appendln("}")
                }
                appendln()
                if (0 == index) {
                    if (matching.lastIndex == index) {
                        //Only one image
                        writeImageCss()
                    } else {
                        //First of set of images
                        appendln("@media only (max-resolution: ${(dpis[size] + dpis[matching[index + 1].key]) / 2}dpi) {")
                        writeImageCss()
                        appendln("}")
                    }
                } else if (matching.lastIndex == index) {
                    //Last of set of images
                    appendln("@media only (min-resolution: ${(dpis[size] + dpis[matching[index - 1].key]) / 2}dpi) {")
                    writeImageCss()
                    appendln("}")
                } else {
                    //Middle image
                    appendln("@media only (min-resolution: ${(dpis[size] + dpis[matching[index - 1].key]) / 2}dpi and max-resolution: ${(dpis[size] + dpis[matching[index + 1].key]) / 2}dpi) {")
                    writeImageCss()
                    appendln("}")
                }
            }
        })
    }
}

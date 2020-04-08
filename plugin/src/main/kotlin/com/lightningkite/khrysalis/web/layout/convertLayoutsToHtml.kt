package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.ios.layout.*
import com.lightningkite.khrysalis.ios.swift.retabSwift
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import java.io.File
import java.lang.StringBuilder

fun convertLayoutsToHtml(
    androidFolder: File,
    webFolder: File,
    converter: HtmlTranslator2 = HtmlTranslator2()
) = convertLayoutsToHtmlRaw(androidFolder.resolve("src/main"), webFolder, converter)

fun convertLayoutsToHtmlRaw(
    androidMainFolder: File,
    webFolder: File,
    converter: HtmlTranslator2 = HtmlTranslator2()
) {

    val styles = androidMainFolder.resolve("res/values/styles.xml").takeIf { it.exists() }?.readXMLStyles() ?: mapOf()
    webFolder.resolve("layouts").apply {
        deleteRecursively()
        mkdirs()
    }

    androidMainFolder.resolve("res/layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            log(item.toString())

            val appendable = StringBuilder()
            converter.styles = styles
            val root = XmlNode.read(item, styles)
            converter.emitFile(root, appendable)
            val output = appendable.toString()

            webFolder.resolve("layouts").resolve(item.nameWithoutExtension + ".html").also{
                it.parentFile.mkdirs()
            }.writeTextIfDifferent(output)
        }
}

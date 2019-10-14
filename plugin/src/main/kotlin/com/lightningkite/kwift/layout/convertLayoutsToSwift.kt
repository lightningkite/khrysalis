package com.lightningkite.kwift.layout

import com.lightningkite.kwift.log
import com.lightningkite.kwift.swift.retabSwift
import com.lightningkite.kwift.utils.camelCase
import java.io.File

fun convertLayoutsToSwift(
    androidFolder: File,
    iosFolder: File,
    converter: LayoutConverter = LayoutConverter.normal
) {

    val styles = androidFolder.resolve("src/main/res/values/styles.xml").readXMLStyles()
    iosFolder.resolve("swiftResources/layouts").apply {
        deleteRecursively()
        mkdirs()
    }

    androidFolder.resolve("src/main/res/layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            log(item.toString())
            val output = item.translateLayoutXml(styles, converter).retabSwift()
            iosFolder.resolve("swiftResources/layouts").resolve(item.nameWithoutExtension.camelCase().capitalize() + "Xml.swift").also{
                it.parentFile.mkdirs()
            }.writeText(output)
        }
}

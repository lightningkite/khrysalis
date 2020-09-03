package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.ios.swift.retabSwift
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
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

    //Load equivalents
    iosFolder.parentFile.walkTopDown()
        .filter {
            it.name.endsWith(".ts.yaml") || it.name.endsWith(".ts.yml")
        }
        .forEach { actualFile ->
            try {
                converter.replacements += actualFile
            } catch (t: Throwable) {
                println("Failed to parse equivalents for $actualFile:")
            }
        }

    androidFolder.resolve("src/main/res/layout").walkTopDown()
        .filter { it.extension == "xml" }
        .filter { !it.name.startsWith("android_") }
        .forEach { item ->
            log(item.toString())
            val output = item.translateLayoutXml(styles, converter).retabSwift()
            iosFolder.resolve("swiftResources/layouts").resolve(item.nameWithoutExtension.camelCase().capitalize() + "Xml.swift").also{
                it.parentFile.mkdirs()
            }.writeTextIfDifferent(output)
        }
}

package com.lightningkite.khrysalis.ios.layout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
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

    val androidFiles = jacksonObjectMapper().readValue<Map<String, AndroidLayoutFile>>(
        androidFolder.resolve("build/layout/summary.json")
    )

    for((name, layout) in androidFiles){
        log("Converting layout ${layout.fileName}.xml")
        val file = androidFolder.resolve("src/main/res/layout/${layout.fileName}.xml")
        val output = file.translateLayoutXml(layout, styles, converter).retabSwift()
        iosFolder.resolve("swiftResources/layouts").resolve(file.nameWithoutExtension.camelCase().capitalize() + "Xml.swift").also{
            it.parentFile.mkdirs()
        }.writeTextIfDifferent(output)
    }
}

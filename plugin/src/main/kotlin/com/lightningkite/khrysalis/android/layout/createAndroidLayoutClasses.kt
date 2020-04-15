package com.lightningkite.khrysalis.android.layout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.log
import java.io.File

fun createAndroidLayoutClasses(androidFolder: File, applicationPackage: String) = createAndroidLayoutClasses(
    resourcesFolder = androidFolder.resolve("src/main/res"),
    applicationPackage = applicationPackage,
    outputFolder = androidFolder.resolve("src/main/java/${applicationPackage.replace('.', '/')}/layouts"),
    buildFileForPrototyper = androidFolder.resolve("build/layout/summary.json")
)

fun readLayoutInfo(buildFolder: File): Map<String, AndroidLayoutFile> =
    jacksonObjectMapper().readValue(buildFolder.resolve("layout/summary.json"))

fun createAndroidLayoutClasses(
    resourcesFolder: File,
    applicationPackage: String,
    outputFolder: File,
    buildFileForPrototyper: File
) {
    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()
    val packageName =
        outputFolder.absolutePath.replace('\\', '/').substringAfter("src/main/").substringAfter('/').replace('/', '.')

    outputFolder.deleteRecursively()
    val layouts = AndroidLayoutFile.parseAll(resourcesFolder, styles)
    buildFileForPrototyper.parentFile.mkdirs()
    buildFileForPrototyper.writeText(jacksonObjectMapper().writeValueAsString(layouts))
    layouts.values.forEach {
        log("Layout " + it.fileName)
        File(outputFolder, it.name + "Xml.kt").also {
            it.parentFile.mkdirs()
        }.writeText(it.toString(packageName, applicationPackage))
    }
}


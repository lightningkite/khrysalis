package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.swift.replacements.Replacements
import com.lightningkite.khrysalis.util.SmartTabWriter
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File

fun convertLayoutsToSwift2(
    androidFolder: File,
    iosFolder: File,
    equivalentsFolders: Sequence<File>
) {

    val styles = androidFolder.resolve("src/main/res/values/styles.xml").readXMLStyles()
    iosFolder.resolve("swiftResources/layouts").apply {
        deleteRecursively()
        mkdirs()
    }

    //Load equivalents
    val replacements = Replacements()
    equivalentsFolders.plus(sequenceOf(iosFolder))
        .flatMap { it.walkTopDown() }
        .filter {
            it.name.endsWith(".swift.yaml") || it.name.endsWith(".swift.yml")
        }
        .forEach { actualFile ->
            try {
                replacements += actualFile
            } catch (t: Throwable) {
                println("Failed to parse equivalents for $actualFile:")
            }
        }

    val converter = AppleResourceLayoutConversion()
    converter.getStrings(androidFolder.resolve("src/main/res/values/strings.xml"))
    converter.getDimensions(androidFolder.resolve("src/main/res/values/dimens.xml"))
    converter.getColors(androidFolder.resolve("src/main/res/values/colors.xml"))
    androidFolder.resolve("src/main/res/color").listFiles()?.forEach {
        converter.getStateColor(it)
    }
    converter.getAndMovePngs(
        resourcesFolder = androidFolder.resolve("src/main/res"),
        assetsFolder = iosFolder.resolve("Assets.xcassets")
    )
    converter.writeColorAssets(assetsFolder = iosFolder.resolve("Assets.xcassets"))
    converter.writeRFile(
        androidResourcesFolder = androidFolder.resolve("src/main/res"),
        baseFolderForLocalizations = iosFolder.resolve("localizations"),
        iosResourcesSwiftFolder = iosFolder.resolve("swiftResources")
    )

    val androidFiles = jacksonObjectMapper().readValue<Map<String, AndroidLayoutFile>>(
        androidFolder.resolve("build/layout/summary.json")
    )

    for((name, layout) in androidFiles){
        log("Converting layout ${layout.fileName}.xml")
        val inputFile = androidFolder.resolve("src/main/res/layout/${layout.fileName}.xml")
        val swiftOutputFile = iosFolder.resolve("swiftResources/layouts")
            .resolve(layout.fileName.camelCase().capitalize() + "Xml.swift")
        val xibOutputFile = iosFolder.resolve("swiftResources/layouts")
            .resolve(layout.fileName + ".xib")
        swiftOutputFile.bufferedWriter().use { bw ->
            layout.toSwift(replacements, SmartTabWriter(bw))
        }
        xibOutputFile.bufferedWriter().use { bw ->
            converter.xibDocument(inputFile, replacements, styles, bw)
        }
    }
}


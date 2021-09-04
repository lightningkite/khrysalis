package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.swift.KotlinSwiftCR
import com.lightningkite.khrysalis.swift.replacements.SwiftJacksonReplacementsModule
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
    val replacements = Replacements(KotlinSwiftCR.replacementMapper)
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
                t.printStackTrace()
            }
        }
    val xibRules = equivalentsFolders.plus(sequenceOf(iosFolder))
        .flatMap { it.walkTopDown() }
        .filter {
            it.name.endsWith("xib.yaml") || it.name.endsWith("xib.yml")
        }
        .flatMap { actualFile ->
            val replacementMapper = ObjectMapper(YAMLFactory())
                .registerModule(XibRulesModule())
                .registerModule(SwiftJacksonReplacementsModule())
                .registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            println(replacementMapper.readTree(actualFile))
            replacementMapper.readValue<List<XibTranslation>>(actualFile)
        }
        .associateBy { it.id }

    val converter = AppleResourceLayoutConversion()
    converter.getFonts(androidFolder.resolve("src/main/res/font"))
    converter.getStrings(androidFolder.resolve("src/main/res/values/strings.xml"))
    converter.getDimensions(androidFolder.resolve("src/main/res/values/dimens.xml"))
    converter.getColors(androidFolder.resolve("src/main/res/values/colors.xml"))
    androidFolder.resolve("src/main/res/color").listFiles()?.forEach {
        converter.getStateColor(it)
    }
    converter.translateDrawables(
        resourcesFolder = androidFolder.resolve("src/main/res"),
        swiftResourcesFolder = iosFolder.resolve("swiftResources")
    )
    converter.getAndMovePngs(
        resourcesFolder = androidFolder.resolve("src/main/res"),
        assetsFolder = iosFolder.resolve("Assets.xcassets")
    )
    converter.getAndTranslateSvgs(
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
    println("Layouts to convert: $androidFiles")
    println("XIB rules: $xibRules")

    for((name, layout) in androidFiles){
        log("Converting layout ${layout.fileName}.xml")
        println("Converting layout ${layout.fileName}.xml")
        val inputFile = androidFolder.resolve("src/main/res/layout/${layout.fileName}.xml")
        val swiftOutputFile = iosFolder.resolve("swiftResources/layouts")
            .resolve(layout.fileName.camelCase().capitalize() + "Xml.swift")
        val xibOutputFile = iosFolder.resolve("swiftResources/layouts")
            .resolve(layout.fileName + ".xib")
        swiftOutputFile.bufferedWriter().use { bw ->
            layout.toSwift(replacements, SmartTabWriter(bw))
        }
        xibOutputFile.bufferedWriter().use { bw ->
            converter.xibDocument(inputFile, XibRules(xibRules, replacements), styles, bw)
        }
    }
}


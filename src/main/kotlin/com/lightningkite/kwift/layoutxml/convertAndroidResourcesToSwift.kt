package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.log
import com.lightningkite.kwift.swift.retabSwift
import com.lightningkite.kwift.utils.camelCase
import java.io.File

fun convertAndroidResourcesToSwift(
    resourcesFolder: File,
    baseFolderForLocalizations: File,
    outputFolder: File
) {
    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()

    val stringBase = File(resourcesFolder, "values/strings.xml").readXMLStrings()
    val stringLocales = resourcesFolder.listFiles()
        .filter { it.name.startsWith("values-") }
        .filter { File(it, "strings.xml").exists() }
        .associate { it.name.substringAfter('-') to File(it, "strings.xml").readXMLStrings() }
    stringBase.let {
        File(outputFolder, "strings.swift").apply{ parentFile.mkdirs() }.writeText(it.writeXMLStrings())
    }
    stringLocales.entries.forEach {
        File(File(baseFolderForLocalizations, "${it.key}.lproj"), "Localizable.strings")
            .apply{ parentFile.mkdirs() }
            .writeText(it.value.writeXMLStringsTranslation(stringBase, it.key))
    }

    File(resourcesFolder, "values/dimens.xml").takeIf { it.exists() }?.readXMLDimen()?.writeXMLDimen()?.let {
        File(outputFolder, "dimen.swift").apply{ parentFile.mkdirs() }.writeText(it)
    }

    val colors = File(resourcesFolder, "values/colors.xml").readXMLColors()
    val colorSets = File(resourcesFolder, "color").walkTopDown()
        .filter { it.extension == "xml" }
        .associate { it.nameWithoutExtension to it.readXMLColorSet(colors) }
    val allColors = colors + colorSets.entries.associate { it.key to (it.value[".normal"] ?: "UIColor.black") }
    File(outputFolder, "colors.swift").apply{ parentFile.mkdirs() }.writeText(allColors.writeXMLColors())

    File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            log(item.toString())
            val output = item.translateLayoutXml(styles).retabSwift()
            File(outputFolder, "layout/" + item.nameWithoutExtension.camelCase().capitalize() + "Xml.swift").also{
                it.parentFile.mkdirs()
            }.apply{ parentFile.mkdirs() }.writeText(output)
        }
}

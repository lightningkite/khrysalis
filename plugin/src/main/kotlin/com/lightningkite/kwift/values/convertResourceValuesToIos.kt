package com.lightningkite.kwift.values

import java.io.File

fun convertResourceValuesToIos(
    androidResourcesFolder: File,
    baseFolderForLocalizations: File,
    iosResourcesSwiftFolder: File
){
    val stringBase = File(androidResourcesFolder, "values/strings.xml").readXMLStrings()
    val stringLocales = (androidResourcesFolder.listFiles() ?: arrayOf())
        .filter { it.name.startsWith("values-") }
        .filter { File(it, "strings.xml").exists() }
        .associate { it.name.substringAfter('-') to File(it, "strings.xml").readXMLStrings() }
    stringBase.let {
        File(iosResourcesSwiftFolder, "strings.swift").apply{ parentFile.mkdirs() }.writeText(it.writeXMLStrings())
    }
    stringLocales.entries.forEach {
        File(File(baseFolderForLocalizations, "${it.key}.lproj"), "Localizable.strings")
            .apply{ parentFile.mkdirs() }
            .writeText(it.value.writeXMLStringsTranslation(stringBase, it.key))
    }

    File(androidResourcesFolder, "values/dimens.xml").takeIf { it.exists() }?.readXMLDimen()?.writeXMLDimen()?.let {
        File(iosResourcesSwiftFolder, "dimen.swift").apply{ parentFile.mkdirs() }.writeText(it)
    }

    val colors = File(androidResourcesFolder, "values/colors.xml").readXMLColors()
    val colorSets = File(androidResourcesFolder, "color").walkTopDown()
        .filter { it.extension == "xml" }
        .associate { it.nameWithoutExtension to it.readXMLColorSet(colors) }
    val allColors = colors + colorSets.entries.associate { it.key to (it.value[".normal"] ?: "UIColor.black") }
    File(iosResourcesSwiftFolder, "colors.swift").apply{ parentFile.mkdirs() }.writeText(allColors.writeXMLColors())
}

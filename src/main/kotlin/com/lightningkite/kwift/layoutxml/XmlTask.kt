package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.swift.retabSwift
import com.lightningkite.kwift.utils.camelCase
import java.io.File

fun xmlTask(
    resourcesFolder: File,
    outputFolder: File
) {
    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()

    File(resourcesFolder, "values/strings.xml").readXMLStrings().writeXMLStrings().let {
        File(outputFolder, "strings.swift").writeText(it)
    }

    File(resourcesFolder, "values/dimen.xml").readXMLDimen().writeXMLDimen().let {
        File(outputFolder, "dimen.swift").writeText(it)
    }

    val colors = File(resourcesFolder, "values/colors.xml").readXMLColors()
    val colorSets = File(resourcesFolder, "color").walkTopDown()
        .filter { it.extension == "xml" }
        .associate { it.nameWithoutExtension to it.readXMLColorSet(colors) }
    val allColors = colors + colorSets.entries.associate { it.key to (it.value[".normal"] ?: "UIColor.black") }
    File(outputFolder, "colors.swift").writeText(allColors.writeXMLColors())

    File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            println(item)
            val output = item.translateLayoutXml(styles).retabSwift()
            println(output)
            File(outputFolder, "layout/" + item.nameWithoutExtension.camelCase().capitalize() + "Xml.swift").also{
                it.parentFile.mkdirs()
            }.writeText(output)
        }
}

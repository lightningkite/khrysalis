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

    File(iosResourcesSwiftFolder, "colors.swift").bufferedWriter().use { out ->
        out.appendln("//")
        out.appendln("// ResourcesColors.swift")
        out.appendln("// Created by Kwift")
        out.appendln("//")
        out.appendln("")
        out.appendln("import Foundation")
        out.appendln("import UIKit")
        out.appendln("import Kwift")
        out.appendln("")
        out.appendln("")
        out.appendln("public enum ResourcesColors {")
        out.appendln("    static let transparent = UIColor.clear")
        out.appendln("    static let black = UIColor.black")
        out.appendln("    static let white = UIColor.white")

        File(androidResourcesFolder, "values/colors.xml").translateXMLColors(out)
        File(androidResourcesFolder, "color").walkTopDown()
            .filter { it.extension == "xml" }
            .forEach { it.translateXmlColorSet(out) }

        out.appendln("}")
    }
}

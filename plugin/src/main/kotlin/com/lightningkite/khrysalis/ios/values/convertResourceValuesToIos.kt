package com.lightningkite.khrysalis.ios.values

import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import java.io.File
import java.io.StringWriter

fun convertResourceValuesToIos(
    androidResourcesFolder: File,
    baseFolderForLocalizations: File,
    iosResourcesSwiftFolder: File
) {
    val stringBase = File(androidResourcesFolder, "values/strings.xml").readXMLStrings()
    val stringLocales = (androidResourcesFolder.listFiles() ?: arrayOf())
        .filter { it.name.startsWith("values-") }
        .filter { File(it, "strings.xml").exists() }
        .associate { it.name.substringAfter('-') to File(it, "strings.xml").readXMLStrings() }
    stringLocales.entries.forEach {
        File(File(baseFolderForLocalizations, "${it.key}.lproj"), "Localizable.strings")
            .apply { parentFile.mkdirs() }
            .writeTextIfDifferent(it.value.writeXMLStringsTranslation(stringBase, it.key))
    }
    iosResourcesSwiftFolder.mkdirs()
    File(iosResourcesSwiftFolder, "R.swift").bufferedWriter().use { out ->
        with(SmartTabWriter(out)) {
            appendLine("//")
            appendLine("// R.swift")
            appendLine("// Created by Khrysalis")
            appendLine("//")
            appendLine("")
            appendLine("import Foundation")
            appendLine("import UIKit")
            appendLine("import LKButterfly")
            appendLine("")
            appendLine("")
            appendLine("public enum R {")

            appendLine("public enum drawable {}")

            appendLine("public enum string {")
            stringBase.writeXMLStrings(this)
            appendLine("}")

            appendLine("public enum dimen {")
            File(androidResourcesFolder, "values/dimens.xml").takeIf { it.exists() }?.readXMLDimen()
                ?.writeXMLDimen(this)
            appendLine("}")

            appendLine("public enum color {")

            appendLine("static let transparent = UIColor.clear")
            appendLine("static let black = UIColor.black")
            appendLine("static let white = UIColor.white")
            File(androidResourcesFolder, "values/colors.xml").translateXMLColors(this)
            File(androidResourcesFolder, "color").walkTopDown()
                .filter { it.extension == "xml" }
                .forEach { it.translateXmlColorSet(this) }
            appendLine("}")

            appendLine("}")
        }
    }
}

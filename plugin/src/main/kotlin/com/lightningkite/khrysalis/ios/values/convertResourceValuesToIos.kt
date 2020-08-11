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
            appendln("//")
            appendln("// R.swift")
            appendln("// Created by Khrysalis")
            appendln("//")
            appendln("")
            appendln("import Foundation")
            appendln("import UIKit")
            appendln("import Khrysalis")
            appendln("")
            appendln("")
            appendln("public enum R {")

            appendln("public enum drawable {}")

            appendln("public enum string {")
            stringBase.writeXMLStrings(this)
            appendln("}")

            appendln("public enum dimen {")
            File(androidResourcesFolder, "values/dimens.xml").takeIf { it.exists() }?.readXMLDimen()
                ?.writeXMLDimen(this)
            appendln("}")

            appendln("public enum color {")

            appendln("static let transparent = UIColor.clear")
            appendln("static let black = UIColor.black")
            appendln("static let white = UIColor.white")
            File(androidResourcesFolder, "values/colors.xml").translateXMLColors(this)
            File(androidResourcesFolder, "color").walkTopDown()
                .filter { it.extension == "xml" }
                .forEach { it.translateXmlColorSet(this) }
            appendln("}")

            appendln("}")
        }
    }
}

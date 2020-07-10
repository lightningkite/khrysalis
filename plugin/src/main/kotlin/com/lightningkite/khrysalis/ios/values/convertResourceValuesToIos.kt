package com.lightningkite.khrysalis.ios.values

import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import java.io.File
import java.io.StringWriter

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
    stringLocales.entries.forEach {
        File(File(baseFolderForLocalizations, "${it.key}.lproj"), "Localizable.strings")
            .apply{ parentFile.mkdirs() }
            .writeTextIfDifferent(it.value.writeXMLStringsTranslation(stringBase, it.key))
    }

    File(iosResourcesSwiftFolder, "colors.swift").writeTextIfDifferent(StringWriter().use { out ->
        out.appendln("//")
        out.appendln("// ResourcesColors.swift")
        out.appendln("// Created by Khrysalis")
        out.appendln("//")
        out.appendln("")
        out.appendln("import Foundation")
        out.appendln("import UIKit")
        out.appendln("import Khrysalis")
        out.appendln("")
        out.appendln("")
        out.appendln("public enum ResourcesColors {")
        out.appendln("    static let transparent = UIColor.clear")
        out.appendln("    static let black = UIColor.black")
        out.appendln("    static let white = UIColor.white")


        out.appendln("}")
    }.toString())

    File(iosResourcesSwiftFolder, "R.swift").bufferedWriter().use { out ->
        out.appendln("//")
        out.appendln("// R.swift")
        out.appendln("// Created by Khrysalis")
        out.appendln("//")
        out.appendln("")
        out.appendln("import Foundation")
        out.appendln("import UIKit")
        out.appendln("import Khrysalis")
        out.appendln("")
        out.appendln("")
        out.appendln("public enum R {")

        out.appendln("public enum string {")
        stringBase.writeXMLStrings(out)
        out.appendln("}")

        out.appendln("public enum dimen {")
        File(androidResourcesFolder, "values/dimens.xml").takeIf { it.exists() }?.readXMLDimen()?.writeXMLDimen(out)
        out.appendln("}")

        out.appendln("public enum color {")
        File(androidResourcesFolder, "values/colors.xml").translateXMLColors(out)
        File(androidResourcesFolder, "color").walkTopDown()
            .filter { it.extension == "xml" }
            .forEach { it.translateXmlColorSet(out) }
        out.appendln("}")

        out.appendln("}")
    }
}

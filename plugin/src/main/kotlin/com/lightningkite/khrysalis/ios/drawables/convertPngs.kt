package com.lightningkite.khrysalis.ios.drawables

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.checksum
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import java.io.File
import java.io.StringWriter
import java.lang.Appendable


fun convertPngs(
    resourcesFolder: File,
    assetsFolder: File,
    swiftFolder: File
) {
    val xmlNames = (resourcesFolder.listFiles() ?: arrayOf())
        .asSequence()
        .filter { it.name.startsWith("drawable") }
        .flatMap { it.walkTopDown() }
        .filter { it.extension == "xml" }
        .map { it.nameWithoutExtension }
        .toSet()
    val pngNames = (resourcesFolder.listFiles() ?: arrayOf())
        .asSequence()
        .filter { it.name.startsWith("drawable") }
        .flatMap { it.walkTopDown() }
        .filter { it.extension == "png" }
        .map { it.nameWithoutExtension }
        .filter { it !in xmlNames }
        .distinct()
        .toList()
        .sortedBy { it }

    pngNames.forEach { pngName ->
        val matching = (resourcesFolder.listFiles() ?: arrayOf())
            .asSequence()
            .filter { it.name.startsWith("drawable") }
            .flatMap { it.walkTopDown() }
            .filter { it.name == pngName + ".png" }

        val one = matching.find { it.parent.contains("drawable-ldpi") || it.parent.contains("drawable-mdpi") }
        val two = matching.find { it.parent.contains("drawable-hdpi") || it.parent.contains("drawable-xhdpi") }
        val three = matching.find { it.parent.contains("drawable-xxhdpi") || it.parent.contains("drawable-xxxhdpi") }

        if (one == null && two == null && three == null) return@forEach

        val iosFolder = assetsFolder.resolve(pngName + ".imageset").apply { mkdirs() }
        jacksonObjectMapper().writeValue(
            iosFolder.resolve("Contents.json"),
            mapOf(
                "info" to mapOf("version" to 1, "author" to "xcode"),
                "images" to listOf(one, two, three).mapIndexed { index, file ->
                    if (file == null) return@mapIndexed null
                    mapOf("idiom" to "universal", "filename" to file.name, "scale" to "${index + 1}x")
                }.filterNotNull()
            )
        )
        listOf(one, two, three).filterNotNull().forEach {
            if(it.checksum() != iosFolder.resolve(it.name).checksum()) {
                it.copyTo(iosFolder.resolve(it.name), overwrite = true)
            }
        }
    }

    try {
        val text = StringWriter().use { writer ->
            writer.appendln("//Automatically created by Khrysalis")
            writer.appendln("import UIKit")
            writer.appendln("import Butterfly")
            writer.appendln("")
            writer.appendln("extension R.drawable {")
            writer.appendln("")
            pngNames.forEach { pngName ->
                val typeName = pngName
                writer.appendln("static let $typeName: Drawable = Drawable { (view: UIView?) -> CALayer in CAImageLayer(UIImage(named: \"$pngName\")) }")
            }
            writer.appendln("")
            writer.appendln("}")
        }.toString()
        swiftFolder.resolve("drawable").also { it.mkdirs() }.resolve("PNGs.swift").writeTextIfDifferent(text)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

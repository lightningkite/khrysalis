package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import java.io.File
import java.io.StringWriter
import java.lang.Appendable
import java.lang.Exception


fun convertDrawableXmls(
    resourcesFolder: File,
    swiftFolder: File
) {
    resourcesFolder.listFiles()!!
        .asSequence()
        .filter { it.name.startsWith("drawable") && it.isDirectory }
        .sortedByDescending { it.name.substringAfter('-').filter { it.isDigit() }.takeUnless { it.isEmpty() }?.toInt() ?: 0 }
        .flatMap { it.walkTopDown().filter { it.extension == "xml" } }
        .distinctBy { it.name }
        .sortedBy { it.name }
        .map { it to XmlNode.read(it, mapOf()) }
        .forEach { (file, it) ->
            val name = file.nameWithoutExtension
            val outputFile = swiftFolder.resolve("drawable").resolve(name + "Drawable.swift")
            println("$file -> $outputFile")
            try {
                if(outputFile.exists()){
                    outputFile.useLines {
                        if(it.firstOrNull() != "//Automatically created by Khrysalis"){
                            return@forEach
                        }
                    }
                }
                outputFile.writeText(StringWriter().use { writer ->
                    writer.appendLine("//Automatically created by Khrysalis")
                    writer.appendLine("import UIKit")
                    writer.appendLine("import LKButterfly")
                    writer.appendLine("")
                    writer.appendLine("extension R.drawable {")
                    writer.appendLine("")
                    convertDrawableXml(name, it, writer)
                    writer.appendLine("")
                    writer.appendLine("}")
                }.toString())
            } catch (e: Exception) {
                println("FAILED $file -> $outputFile")
                e.printStackTrace()
            }
        }
}

fun convertDrawableXml(name: String, node: XmlNode, out: Appendable) {
    when (node.name.toLowerCase()) {
        "selector" -> convertSelectorDrawable(
            name,
            node,
            out
        )
        "shape" -> convertShapeDrawable(
            name,
            node,
            out
        )
        "layer-list" -> convertLayerListDrawable(
            name,
            node,
            out
        )
        "vector" -> convertVectorDrawable(
            name,
            node,
            out
        )
        "bitmap" -> convertBitmapDrawable(
            name,
            node,
            out
        )
        else -> {
            out.appendLine("// WARNING: Could not convert drawable of type ${node.name.toLowerCase()}")
            out.appendLine("static let $name: Drawable = Drawable { (view: UIView?) -> CALayer in CALayer() }")
        }
    }
}


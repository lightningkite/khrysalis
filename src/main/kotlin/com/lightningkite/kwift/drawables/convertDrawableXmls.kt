package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.*
import java.io.File
import java.lang.Exception


fun convertDrawableXmls(
    resourcesFolder: File,
    swiftFolder: File
) {
    swiftFolder.resolve("drawable").let {
        it.listFiles()?.forEach { it.delete() }
        it.mkdirs()
    }
    resourcesFolder.resolve("drawable").walkTopDown()
        .filter { it.extension == "xml" }
        .map { it to XmlNode.read(it, mapOf()) }
        .forEach { (file, it) ->
            val name = file.nameWithoutExtension.camelCase().capitalize()
            val outputFile = swiftFolder.resolve("drawable").resolve(name + "Drawable.swift")
            println("$file -> $outputFile")
            try {
                when (it.name.toLowerCase()) {
                    "selector" -> convertSelectorDrawable(
                        name,
                        it,
                        outputFile
                    )
                    "shape" -> convertShapeDrawable(
                        name,
                        it,
                        outputFile
                    )
                    "layer-list" -> convertLayerListDrawable(
                        name,
                        it,
                        outputFile
                    )
                    "vector" -> convertVectorDrawable(
                        name,
                        it,
                        outputFile
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}


fun convertSelectorDrawable(name: String, node: XmlNode, destination: File) {
    //TODO
}

fun convertShapeDrawable(name: String, node: XmlNode, destination: File) {
    val className = if (node.children.any { it.name == "gradient" }) "CAGradientLayer" else "CALayer"
    destination.writeText(
        buildString {
            appendln("//Automatically created by Kwift")
            appendln("import UIKit")
            appendln("")
            appendln("func $name() -> $className {")
            appendln("    let layer = $className()")
            node.children.find { it.name == "stroke" }?.let {
                appendln("    layer.borderWidth = ${it.attributeAsDimension("android:width") ?: "0"}")
                appendln("    layer.borderColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor")
            }
            node.children.find { it.name == "solid" }?.let {
                appendln("    layer.backgroundColor = ${it.attributeAsColor("android:color") ?: "UIColor.black"}.cgColor")
            }
            node.children.find { it.name == "corners" }?.let {
                appendln("    layer.cornerRadius = ${it.attributeAsDimension("android:radius") ?: "0"}")
            }
            node.children.find { it.name == "gradient" }?.let {
                val colors = listOfNotNull(
                    it.attributeAsColor("android:startColor"),
                    it.attributeAsColor("android:centerColor"),
                    it.attributeAsColor("android:endColor")
                )
                appendln("    layer.colors = " + colors.joinToString(", ", "[", "]") { "$it.cgColor" })
                val angle = it.attributeAsInt("android:angle") ?: 0
                appendln("    layer.transform = CATransform3DMakeRotation(CGFloat.pi * $angle / 180, 0, 0, 1)")
            }
            appendln("    return layer")
            appendln("}")
        }
    )
}

fun convertLayerListDrawable(name: String, node: XmlNode, destination: File) {
    destination.writeText(
        buildString {
            appendln("//Automatically created by Kwift")
            appendln("import UIKit")
            appendln("")
            appendln("func $name() -> CALayer {")
            appendln("    let layer = CALayer()")
            for (subnode in node.children) {
                appendln("    layer.addSublayer({")
                appendln("        let sublayer = ${subnode.attributeAsLayer("android:drawable") ?: "CALayer() /* Unknown */"}")
                subnode.attributeAsDimension("android:width")?.let {
                    appendln("        sublayer.frame.size.width = $it")
                }
                subnode.attributeAsDimension("android:height")?.let {
                    appendln("        sublayer.frame.size.height = $it")
                }
                appendln("        return sublayer")
                appendln("    }())")
            }
            appendln("    return layer")
            appendln("}")
        }
    )
}

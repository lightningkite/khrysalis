package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.ios.layout2.models.IosColor
import com.lightningkite.khrysalis.ios.layout2.models.IosDrawable
import com.lightningkite.khrysalis.ios.layout2.models.StateSelector
import com.lightningkite.khrysalis.ios.values.*
import com.lightningkite.khrysalis.swift.replacements.*
import com.lightningkite.khrysalis.swift.replacements.xib.*
import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import com.lightningkite.khrysalis.utils.*
import java.io.File
import java.lang.IllegalStateException

class AppleResourceLayoutConversion() {
    var styles: Styles = mapOf()
    val colors: MutableMap<String, StateSelector<IosColor>> = HashMap()
    val images: MutableMap<String, StateSelector<IosDrawable>> = HashMap()
    val strings: MutableMap<String, String> = HashMap()
    val dimensions: MutableMap<String, String> = HashMap()

    fun getStrings(file: File){
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "string" }
            .forEach {
                strings[it.allAttributes["name"] ?: "noname"] = it.element.textContent
            }
    }


    fun getDimensions(file: File) {
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "dimen" }
            .forEach {
                dimensions[it.allAttributes["name"] ?: "noname"] = it.element.textContent
            }
    }

    fun getColors(file: File){
        val ignored = setOf("white", "black", "transparent")
        val colorsToProcess = ArrayList<Pair<String, IosColor>>()
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "color" }
            .filter { it.allAttributes["name"] !in ignored }
            .forEach {
                val raw = it.element.textContent
                val name = (it.allAttributes["name"] ?: "noname").safeSwiftIdentifier()
                when{
                    raw.startsWith("@color/") -> {
                        val colorName = raw.removePrefix("@color/")
                        colorsToProcess.add(name to IosColor(referenceTo = colorName))
                    }
                    raw.startsWith("@android:color/") -> {
                        val colorName = raw.removePrefix("@android:color/")
                        colorsToProcess.add(name to IosColor(referenceTo = colorName))
                    }
                    raw.startsWith("#") -> {
                        this.colors[name] = StateSelector(IosColor.fromHashString(raw)!!)
                    }
                    else -> {}
                }
            }
        while(colorsToProcess.isNotEmpty()){
            val popped = colorsToProcess.removeAt(0)
            this.colors[popped.second.referenceTo!!]?.let {
                this.colors[popped.first] = StateSelector(it.normal.copy(referenceTo = popped.second.referenceTo))
            } ?: colorsToProcess.find { it.first == popped.second.referenceTo }?.let {
                colorsToProcess.add(popped.first to it.second)
            }
        }
    }

    fun getStateColor(file: File) {
        var normal: IosColor = IosColor.transparent
        var selected: IosColor? = null
        var highlighted: IosColor? = null
        var disabled: IosColor? = null
        var focused: IosColor? = null
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "item" }
            .forEach { subnode ->
                val raw = subnode.allAttributes["android:color"] ?: ""
                val c: IosColor? = when{
                    raw.startsWith("@color/") -> {
                        val colorName = raw.removePrefix("@color/")
                        colors[colorName]?.normal
                    }
                    raw.startsWith("@android:color/") -> {
                        val colorName = raw.removePrefix("@android:color/")
                        colors[colorName]?.normal
                    }
                    raw.startsWith("#") -> IosColor.fromHashString(raw)
                    else -> null
                }
                when {
                    subnode.attributeAsBoolean("android:state_enabled") == false -> disabled = c
                    subnode.attributeAsBoolean("android:state_pressed") == true -> highlighted = c
                    subnode.attributeAsBoolean("android:state_checked") == true -> selected = c
                    subnode.attributeAsBoolean("android:state_selected") == true -> selected = c
                    subnode.attributeAsBoolean("android:state_focused") == true -> focused = c
                    c != null -> normal = c
                }
            }
        colors[file.nameWithoutExtension] = StateSelector(
            normal = normal,
            selected = selected,
            highlighted = highlighted,
            disabled = disabled,
            focused = focused
        )
    }

    fun writeColorAssets(
        assetsFolder: File
    ) {
        assetsFolder.mkdirs()
        val mapper = jacksonObjectMapper()
        for((k, v) in this.colors){
            for((vName, vValue) in v.variants) {
                assetsFolder.resolve("color_$k$vName").apply { mkdirs() }.resolve("Contents.json").writeText(
                    mapper.writeValueAsString(
                        mapOf<String, Any?>(
                            "colors" to listOf(
                                mapOf(
                                    "color" to mapOf(
                                        "color-space" to "srgb",
                                        "components" to mapOf(
                                            "alpha" to vValue.alpha.toString(),
                                            "red" to vValue.red.toString(),
                                            "green" to vValue.green.toString(),
                                            "blue" to vValue.blue.toString()
                                        )
                                    ),
                                    "idiom" to "universal"
                                )
                            ),
                            "info" to mapOf(
                                "author" to "khrysalis",
                                "version" to 1
                            )
                        )
                    )
                )
            }
        }
    }

    fun getAndMovePngs(
        resourcesFolder: File,
        assetsFolder: File
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
            images[pngName] = StateSelector(IosDrawable(pngName))
        }
    }

    fun String.asIosColorSet(): Pair<String?, StateSelector<IosColor>>? {
        val raw = this
        return when{
            raw.startsWith("@color/") -> {
                val colorName = raw.removePrefix("@color/")
                colors[colorName]?.let { colorName to it }
            }
            raw.startsWith("@android:color/") -> {
                val colorName = raw.removePrefix("@android:color/")
                colors[colorName]?.let { colorName to it }
            }
            raw.startsWith("#") -> IosColor.fromHashString(raw)?.let { null to StateSelector(it) }
            else -> null
        }
    }

    fun writeRFile(
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
                appendln("import Butterfly")
                appendln("")
                appendln("")
                appendln("public enum R {")

                appendln("public enum drawable {")
                for(entry in images.entries){
                    appendln("static let ${entry.key}: Drawable = Drawable { (view: UIView?) -> CALayer in CAImageLayer(UIImage(named: \"${entry.key}.png\")) }")
                }
                appendln("}")

                appendln("public enum string {")
                for((key, value) in strings.entries){
                    val fixedString = value
                        .replace("\\'", "'")
                        .replace("\\$", "$")
                        .replace(Regex("\n *"), " ")
                    out.appendln("static let ${key.safeSwiftIdentifier()} = NSLocalizedString(\"$fixedString\", comment: \"$key\")")
                }
                appendln("}")

                appendln("public enum dimen {")
                for ((key, value) in dimensions.entries) {
                    if (key.contains("programmatic", true)) {
                        out.appendln("static var ${key.safeSwiftIdentifier()}: CGFloat = $value")
                    } else {
                        out.appendln("static let ${key.safeSwiftIdentifier()}: CGFloat = $value")
                    }
                }
                appendln("}")

                appendln("public enum color {")

                appendln("static let transparent = UIColor.clear")
                appendln("static let black = UIColor.black")
                appendln("static let white = UIColor.white")
                for(entry in colors.entries){
                    if(entry.value.isSet){
                        out.appendln("static let ${entry.key}: UIColor = UIColor(named: \"color_${entry.key}\")")
                    } else {
                        out.appendln("static let ${entry.key}: StateSelector<UIColor> = StateSelector(normal: ${entry.value.normal}, highlighted: ${entry.value.highlighted ?: "nil"}, selected: ${entry.value.selected ?: "nil"}, disabled: ${entry.value.disabled ?: "nil"}, focused: ${entry.value.focused ?: "nil"})")
                    }
                }
                appendln("}")

                appendln("}")
            }
        }
    }

    private inner class Resolver(): CanResolveValue {
        val usedColors = HashSet<String>()
        val usedImages = HashSet<String>()

        override fun resolveFont(string: String): String {
            return string.substringAfter('/')
        }

        override fun resolveDimension(string: String): String {
            if(string.startsWith("@"))
                return dimensions[string.substringAfter('/')] ?: "0"
            else
                return string
        }

        override fun resolveColor(string: String): Any {
            if(string.startsWith("@")) {
                val c = "color_" + string.substringAfter('/')
                usedColors.add(c)
                return c
            } else
                return IosColor.fromHashString(string)!!
        }

        override fun resolveString(string: String): String {
            if(string.startsWith("@"))
                return strings[string.substringAfter('/')]!!
            else
                return string
        }

        override fun resolveImage(string: String): String {
            val i = string.substringAfter('/')
            usedImages.add(i)
            return i
        }
    }

    fun xibDocument(
        inputFile: File,
        replacements: Replacements,
        styles: Map<String, Map<String, String>>,
        out: Appendable
    ) {
        val resolver = Resolver()
        val rootNode = XmlNode.read(inputFile, styles)

        val idsToStore = HashSet<String>()
        fun idPass(node: XmlNode){
            node.allAttributes["android:id"]?.substringAfter('/')?.let {
                node.tags["id"] = it
                idsToStore.add(it)
            } ?: run {
                node.tags["id"] = makeId()
            }
            for(child in node.children){
                idPass(node)
            }
        }
        idPass(rootNode)

        val rootView = replacements.translate(resolver, rootNode)
        out.appendln("""
            <?xml version="1.0" encoding="UTF-8"?>
            <document type="com.apple.InterfaceBuilder3.CocoaTouch.XIB" version="3.0" toolsVersion="17156" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES">
                <device id="retina3_5" orientation="portrait" appearance="light"/>
                <dependencies>
                    <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="17125"/>
                    <capability name="Safe area layout guides" minToolsVersion="9.0"/>
                    <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
                </dependencies>
                <objects>""")
        out.appendln("""
            <placeholder placeholderIdentifier="IBFilesOwner" id="-1" customClass="${inputFile.nameWithoutExtension.camelCase()}XML" customModuleProvider="target">
            <connections>
        """.trimIndent())
        for(id in idsToStore){
            out.appendln("""<outlet property="$id" destination="$id" id="${makeId()}"/>""")
        }
        out.appendln("""
            </connections>
            </placeholder>
        """.trimIndent())
        out.appendln("""
            <placeholder placeholderIdentifier="IBFirstResponder" id="-2" customClass="UIResponder"/>
        """.trimIndent())
        rootView.write(out)
        out.appendln("""
                </objects>
                <resources>
        """.trimIndent())
        for(c in resolver.usedColors){
            out.appendln("""<named-color name="$c" />""")
        }
        for(i in resolver.usedImages){
            out.appendln("""<image name="$i" />""")
        }
        out.appendln("""
                </resources>
            </document>
        """.trimIndent())
    }
}

fun AttHandler.invoke(resolver: CanResolveValue, attr: XmlNode.Attribute, view: PureXmlOut){
    when(kind){
        AttHandlerKind.Direct -> {
            val followed = path!!.resolve(view)
            this.constant?.let {
                followed.put(AttKind.Raw, it, resolver)
            } ?: run {
                followed.put(asKind!!, attr.value, resolver)
            }
        }
        AttHandlerKind.ValueMap -> {
            attr.value
                .splitToSequence('|')
                .map { it.trim() }
                .forEach {
                    val instructions = this.mapValues!![it]
                    if(instructions != null) {
                        for((path, value) in instructions){
                            path.resolve(view).put(AttKind.Raw, value, resolver)
                        }
                    }
                }
        }
        AttHandlerKind.Multiple -> {
            for(sub in multiple!!){
                sub.invoke(resolver, attr, view)
            }
        }
    }
}
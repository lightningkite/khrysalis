package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.ios.drawables.convertDrawableXmls
import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.ios.layout2.models.IosColor
import com.lightningkite.khrysalis.ios.layout2.models.IosDrawable
import com.lightningkite.khrysalis.ios.layout2.models.StateSelector
import com.lightningkite.khrysalis.ios.values.readXMLStrings
import com.lightningkite.khrysalis.ios.values.writeXMLStringsTranslation
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.layout.drawables.androidVectorToSvg
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.mabb.fontverter.FontVerter
import java.io.File
import java.io.StringReader


class AppleResourceLayoutConversion() {
    var styles: Styles = mapOf()
    val colors: MutableMap<String, StateSelector<IosColor>> = hashMapOf()
    val images: MutableMap<String, StateSelector<IosDrawable>> = HashMap()
    val vectors: MutableMap<String, StateSelector<IosDrawable>> = HashMap()
    val fonts: MutableMap<String, IosFont> = HashMap()
    val drawables: MutableSet<String> = HashSet()
    val strings: MutableMap<String, String> = HashMap()
    val dimensions: MutableMap<String, String> = HashMap()

    fun getStrings(file: File) {
        if (!file.exists()) return
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "string" }
            .forEach {
                strings[it.allAttributes["name"] ?: "noname"] = it.element.textContent
            }
    }

    fun getFonts(folder: File) {
        if (!folder.isDirectory) return
        println("Looking for fonts in ${folder}...")
        //fonts themselves first
        folder.listFiles()!!
            .filter { it.extension.toLowerCase() == "otf" || it.extension.toLowerCase() == "ttf" }
            .forEach { file ->
                try {
                    val font = FontVerter.readFont(file)
                    if (!font.isValid) {
                        font.normalize()
                    }
                    val iosFont = IosFont(
                        family = font.properties.family.filter { it in ' '..'~' },
                        name = font.name.filter { it in '!'..'~' },
                        file = file
                    )
                    println("Found font $iosFont")
                    fonts[file.nameWithoutExtension] = iosFont
                } catch(e: Exception) {
                    println("Font read failed for $file")
                    e.printStackTrace()
                    fonts[file.nameWithoutExtension] = IosFont(
                        family = file.nameWithoutExtension,
                        name = file.nameWithoutExtension,
                        file = file
                    )
                }
            }
        folder.listFiles()!!
            .filter { it.extension.toLowerCase() == "xml" }
            .forEach { file ->
                println("Found font set $file")
                val xml = XmlNode.read(file, mapOf())
                xml.children
                    .filter { it.name == "font" }
                    .map { it.allAttributes["android:font"] }
                    .forEach {
                        val name = it!!.substringAfter('/')
                        fonts[file.nameWithoutExtension] =
                            fonts[name] ?: throw IllegalArgumentException("No font $name found")
                    }
            }
    }

    fun getDimensions(file: File) {
        if (!file.exists()) return
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "dimen" }
            .forEach {
                dimensions[it.allAttributes["name"] ?: "noname"] = it.element.textContent
            }
    }

    fun getColors(file: File) {
        if (!file.exists()) return
        val colorsToProcess = ArrayList<Pair<String, IosColor>>()
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "color" }
            .forEach {
                val raw = it.element.textContent
                val name = (it.allAttributes["name"] ?: "noname").safeSwiftIdentifier()
                when {
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
                    else -> {
                    }
                }
            }
        while (colorsToProcess.isNotEmpty()) {
            val popped = colorsToProcess.removeAt(0)
            this.colors[popped.second.referenceTo!!]?.let {
                this.colors[popped.first] = StateSelector(it.normal.copy(referenceTo = popped.second.referenceTo))
            } ?: colorsToProcess.find { it.first == popped.second.referenceTo }?.let {
                colorsToProcess.add(popped.first to it.second)
            }
        }
    }

    fun getStateColor(file: File) {
        if (!file.exists()) return
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
                val c: IosColor? = when {
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
        for ((k, v) in this.colors) {
            for ((vName, vValue) in v.variants) {
                assetsFolder.resolve("color_$k$vName.colorset").apply { mkdirs() }.resolve("Contents.json").writeText(
                    mapper.writeValueAsString(
                        mapOf<String, Any?>(
                            "colors" to listOf(
                                mapOf(
                                    "color" to mapOf(
                                        "color-space" to "srgb",
                                        "components" to mapOf(
                                            "alpha" to vValue.alpha.toString(),
                                            "red" to "0x" + vValue.red.times(255).toInt().toString(16).toUpperCase()
                                                .padStart(
                                                    2,
                                                    '0'
                                                ),
                                            "green" to "0x" + vValue.green.times(255).toInt().toString(16).toUpperCase()
                                                .padStart(
                                                    2,
                                                    '0'
                                                ),
                                            "blue" to "0x" + vValue.blue.times(255).toInt().toString(16).toUpperCase()
                                                .padStart(
                                                    2,
                                                    '0'
                                                )
                                        )
                                    ),
                                    "idiom" to "universal"
                                )
                            ),
                            "info" to mapOf(
                                "author" to "xcode",
                                "version" to 1
                            )
                        )
                    )
                )
            }
        }
    }

    fun translateDrawables(
        resourcesFolder: File,
        swiftResourcesFolder: File
    ) {
        convertDrawableXmls(
            resourcesFolder = resourcesFolder,
            swiftFolder = swiftResourcesFolder
        )
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

        drawables.addAll(xmlNames)
        drawables.addAll(pngNames)

        pngNames.forEach { pngName ->
            val matching = (resourcesFolder.listFiles() ?: arrayOf())
                .asSequence()
                .filter { it.name.startsWith("drawable") }
                .flatMap { it.walkTopDown() }
                .filter { it.name == pngName + ".png" }

            val one = matching.find { it.parent.contains("drawable-ldpi") || it.parent.contains("drawable-mdpi") }
            val two = matching.find { it.parent.contains("drawable-hdpi") || it.parent.contains("drawable-xhdpi") }
            val three =
                matching.find { it.parent.contains("drawable-xxhdpi") || it.parent.contains("drawable-xxxhdpi") }

            if (one == null && two == null && three == null) return@forEach

            val iosFolder = assetsFolder.resolve(pngName + ".imageset").apply { mkdirs() }
            jacksonObjectMapper().writeValue(
                iosFolder.resolve("Contents.json"),
                PngJsonContents(images = listOf(one, two, three).mapIndexed { index, file ->
                    if (file == null) return@mapIndexed null
                    PngJsonContents.Image(filename = file.name, scale = "${index + 1}x")
                }.filterNotNull())
            )
            listOf(one, two, three).filterNotNull().forEach {
                if (it.checksum() != iosFolder.resolve(it.name).checksum()) {
                    it.copyTo(iosFolder.resolve(it.name), overwrite = true)
                }
            }
            images[pngName] = StateSelector(IosDrawable(pngName))
        }
    }

    data class SvgInfo(val width: Double, val height: Double, val asText: String)
    data class PngJsonContents(
        val info: Info = Info(),
        val images: List<Image> = listOf()
    ) {
        data class Info(val version: Int = 1, val author: String = "xcode")
        data class Image(val filename: String, val scale: String = "1x", val idiom: String = "universal")
    }

    fun getAndTranslateSvgs(
        resourcesFolder: File,
        assetsFolder: File
    ) {
        (resourcesFolder.listFiles() ?: arrayOf())
            .asSequence()
            .filter { it.name.startsWith("drawable") }
            .flatMap { it.walkTopDown() }
            .filter { it.extension == "xml" }
            .map { it.nameWithoutExtension to XmlNode.read(it, mapOf()) }
            .filter { it.second.name == "vector" }
            .map {
                val node = it.second
                it.first to SvgInfo(
                    width = node.attributeAsDouble("android:width") ?: 24.0,
                    height = node.attributeAsDouble("android:height") ?: 24.0,
                    asText = buildString {
                        androidVectorToSvg(node, { value ->
                            when {
                                value.startsWith("@") -> {
                                    val found = (colors[value.substringAfter('/')]
                                        ?: throw IllegalArgumentException("Could not find color '$value'")).normal
                                    found.webColor()
                                }
                                value.startsWith("#") -> {
                                    when (value.length - 1) {
                                        3 -> "#" + value[1].toString().repeat(2) + value[2].toString()
                                            .repeat(2) + value[3].toString()
                                            .repeat(2)
                                        4 -> "#" + value[2].toString().repeat(2) + value[3].toString()
                                            .repeat(2) + value[4].toString().repeat(2)
                                        6 -> value
                                        8 -> "#" + value.drop(3).take(6)
                                        else -> "#000000"
                                    }
                                }
                                else -> {
                                    throw IllegalArgumentException("Could not resolve color '$value'.")
                                }
                            }
                        }, this)
                    }
                )
            }
            .forEach {
                val iosFolder = assetsFolder.resolve(it.first + ".imageset").apply { mkdirs() }

                jacksonObjectMapper().writeValue(
                    iosFolder.resolve("Contents.json"),
                    PngJsonContents(images = listOf(1, 2, 3)
                        .mapNotNull { scale ->
                            try {
                                val t = PNGTranscoder()
                                t.addTranscodingHint(
                                    PNGTranscoder.KEY_WIDTH,
                                    it.second.width.times(scale).toFloat()
                                )
                                t.addTranscodingHint(
                                    PNGTranscoder.KEY_HEIGHT,
                                    it.second.height.times(scale).toFloat()
                                )
                                val input = TranscoderInput(StringReader(it.second.asText))
                                val outFile = iosFolder.resolve("${it.first}${scale}x.png")
                                val output = TranscoderOutput(outFile.outputStream())
                                t.transcode(input, output)
                                PngJsonContents.Image(filename = outFile.name, scale = "${scale}x")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        }
                    )
                )
                vectors[it.first] = StateSelector(IosDrawable(it.first))
            }
    }

    fun writeRFile(
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

                appendLine("public enum drawable {")
                for (entry in images.entries) {
                    appendLine("static let ${entry.key}: Drawable = Drawable { (view: UIView?) -> CALayer in CAImageLayer(UIImage(named: \"${entry.key}.png\")) }")
                }
                for (entry in vectors.entries) {
                    appendLine("//Vector ${entry.key} is present as an image and as a drawable in a separate file")
                }
                appendLine("static let allEntries: Dictionary<String, Drawable> = [")
                var firstDrawable = true
                drawables.forEachBetween(
                    forItem = { entry ->
                        append("\"$entry\": $entry")
                    },
                    between = { appendLine(",") }
                )
                appendln()
                appendLine("]")
                appendLine("}")

                appendLine("public enum string {")
                for ((key, value) in strings.entries) {
                    val fixedString = value
                        .replace("\\'", "'")
                        .replace("\\$", "$")
                        .replace(Regex("\n *"), " ")
                    out.appendLine("static let ${key.safeSwiftIdentifier()} = NSLocalizedString(\"$fixedString\", comment: \"$key\")")
                }
                appendLine("}")

                appendLine("public enum dimen {")
                for ((key, value) in dimensions.entries) {
                    if (key.contains("programmatic", true)) {
                        out.appendLine("static var ${key.safeSwiftIdentifier()}: CGFloat = $value")
                    } else {
                        out.appendLine("static let ${key.safeSwiftIdentifier()}: CGFloat = $value")
                    }
                }
                appendLine("}")

                appendLine("public enum color {")

                for (entry in colors.entries) {
                    if (entry.value.isSet) {
                        out.appendLine("static let ${entry.key}: UIColor = UIColor(named: \"color_${entry.key}\")")
                    } else {
                        out.appendLine("static let ${entry.key}: UIColor = ${entry.value.normal}")
                        out.appendLine("static let ${entry.key}State: StateSelector<UIColor> = StateSelector(normal: ${entry.value.normal}, selected: ${entry.value.selected ?: "nil"}, disabled: ${entry.value.disabled ?: "nil"}, highlighted: ${entry.value.highlighted ?: "nil"}, focused: ${entry.value.focused ?: "nil"})")
                    }
                }
                appendLine("}")

                appendLine("}")
            }
        }
    }

    private inner class Resolver() : CanResolveValue {
        val usedColors = HashSet<String>()
        val usedImages = HashSet<String>()

        override fun resolveFont(string: String): IosFont? {
            return fonts[string.substringAfter('/')]
        }

        override fun resolveDimension(string: String): String {
            if (string.startsWith("@"))
                return dimensions[string.substringAfter('/')] ?: "0"
            else
                return string.filter { it.isDigit() || it == '.' || it == '-' }
        }

        override fun resolveColor(string: String): Any {
            if (string.startsWith("@")) {
                val c = "color_" + string.substringAfter('/')
                usedColors.add(c)
                return c
            } else if (string.startsWith("#")) {
                return IosColor.fromHashString(string)!!
            } else {
                throw IllegalArgumentException("Could not resolve color '$string'.")
            }
        }

        override fun resolveString(string: String): String {
            if (string.startsWith("@"))
                return strings[string.substringAfter('/')]!!
            else
                return string
        }

        override fun resolveImage(string: String): String? {
            val ref = string.substringAfter('/')
            if (images.containsKey(ref) || vectors.containsKey(ref)) {
                usedImages.add(ref)
                return ref
            } else {
                println("WARNING - Could not find image '$string'")
                return null
            }
        }

        override fun resolveDrawable(string: String): String? {
            if (drawables.contains(string)) {
                val i = string.substringAfter('/')
//                usedImages.add(i)
                return i
            } else {
                return null
            }
        }
    }

    fun xibDocument(
        inputFile: File,
        xibRules: XibRules,
        styles: Map<String, Map<String, String>>,
        out: Appendable
    ) {
        val resolver = Resolver()
        val rootNode = XmlNode.read(inputFile, styles)

        val idsToStore = HashSet<String>()
        fun idPass(node: XmlNode) {
            node.allAttributes["android:id"]?.substringAfter('/')?.let {
                node.tags["id"] = it
                idsToStore.add(it)
            } ?: run {
                node.tags["id"] = makeId()
            }
            for (child in node.children) {
                idPass(child)
            }
        }
        idPass(rootNode)

        val rootView = xibRules.translate(resolver, rootNode)
        out.appendLine(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <document type="com.apple.InterfaceBuilder3.CocoaTouch.XIB" version="3.0" toolsVersion="18122" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES">
                    <device id="retina3_5" orientation="portrait" appearance="light"/>
                    <dependencies>
                        <deployment identifier="iOS"/>
                        <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="18093"/>
                        <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
                    </dependencies>
                    <objects>""".trimIndent()
        )
        out.appendLine(
            """
                <placeholder placeholderIdentifier="IBFilesOwner" id="-1" customClass="${
                inputFile.nameWithoutExtension.camelCase().capitalize()
            }Xml" customModuleProvider="target">
                <connections>
            """.trimIndent()
        )
        for (id in idsToStore) {
            out.appendLine("""<outlet property="$id" destination="$id" id="${makeId()}"/>""")
        }
        out.appendLine(
            """
                </connections>
                </placeholder>
            """.trimIndent()
        )
        out.appendLine(
            """
                <placeholder placeholderIdentifier="IBFirstResponder" id="-2" customClass="UIResponder"/>
            """.trimIndent()
        )
        rootView.write(out)
        out.appendLine(
            """
                    </objects>
                    <resources>
            """.trimIndent()
        )
        for (c in resolver.usedColors) {
            out.appendLine("""<namedColor name="$c" />""")
        }
        for (i in resolver.usedImages) {
            out.appendLine("""<image name="$i" />""")
        }
        out.appendLine(
            """
                </resources>
                </document>
            """.trimIndent()
        )
    }
}

fun AttHandler.invoke(resolver: CanResolveValue, attr: XmlNode.Attribute, view: PureXmlOut) {
    when (kind) {
        AttHandlerKind.Direct -> {
            val followed = path!!.resolve(view)
            this.constant?.let {
                followed.put(asKind ?: AttKind.Raw, it, resolver)
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
                    if (instructions != null) {
                        for ((path, value) in instructions) {
                            path.resolve(view).put(value.type, value.value, resolver)
                        }
                    }
                }
        }
        AttHandlerKind.Multiple -> {
            for (sub in multiple!!) {
                sub.invoke(resolver, attr, view)
            }
        }
    }
}
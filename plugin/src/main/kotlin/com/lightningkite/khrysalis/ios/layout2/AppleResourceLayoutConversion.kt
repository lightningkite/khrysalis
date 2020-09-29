package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.ios.drawables.*
import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.ios.layout2.models.IosColor
import com.lightningkite.khrysalis.ios.layout2.models.IosDrawable
import com.lightningkite.khrysalis.ios.layout2.models.StateSelector
import com.lightningkite.khrysalis.ios.layout2.models.SubstateAppendable
import com.lightningkite.khrysalis.ios.values.*
import com.lightningkite.khrysalis.swift.replacements.*
import com.lightningkite.khrysalis.swift.replacements.xib.*
import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import com.lightningkite.khrysalis.utils.*
import java.io.File
import java.io.StringWriter
import java.lang.Appendable
import java.lang.Exception

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

    fun resolve(key: String, on: Any?): Any? {
        return when(on){
            is XmlNode -> when(key){
                "type", "name" -> on.name
                else -> on.attribute(key)
            }
            is XmlNode.Attribute -> when(key) {
                "color" -> on.value.asIosColorSet()
                "string" -> if(on.value.startsWith("@")) strings[on.value.substringAfter('/')] else on.value
                "image" -> {
                    val ref = on.value.substringAfter('/')
                    if(images.containsKey(ref)) ref else ""
                }
                "font" -> on.value.substringAfter('/')
                "dim", "dimension" -> (if(on.value.startsWith("@")) dimensions[on.value.substringAfter('/')] else on.value)?.filter { it.isDigit() || it == '-' || it == '.' }
                "value" -> on.value
                "node" -> on.parent
                "parent" -> on.parent
                else -> null
            }
            is StateSelector<*> -> when(key){
                "normal" -> on.normal
                "selected" -> on.selected
                "highlighted" -> on.highlighted
                "disabled" -> on.disabled
                "focused" -> on.focused
                else -> resolve(key, on.normal)
            }
            is IosColor -> when(key){
                "alpha" -> on.alpha
                "red" -> on.red
                "green" -> on.green
                "blue" -> on.blue
                "referenceTo" -> on.referenceTo
                else -> null
            }
            else -> null
        }
    }

    fun templateForElement(element: XmlNode, attr: XmlNode.Attribute? = null): (Template)->String {
        return { t ->
            t.parts.map {
                when(it){
                    is TemplatePart.Text -> it.string
                    is TemplatePart.Parameter -> {
                        val parts = it.name.split('.')
                        var current: Any? = attr ?: element
                        for(part in parts){
                            current = resolve(part, current)
                        }
                        current?.toString() ?: ""
                    }
                    is TemplatePart.Value -> attr?.value ?: element.name
                    else -> ""
                }
            }.joinToString("")
        }
    }

    fun xibDocument(file: File, replacements: Replacements, styles: Styles): XibDocument {
        val xml = XmlNode.read(file, styles)
        val outletList = HashMap<String, XibView>()
        val resources = HashMap<String, XibResource>()
        val owner = XibOwner(
            type = XibClassReference(name = file.name.camelCase() + "Xml"),
            outlets = outletList
        )
        fun translate(element: XmlNode, parent: XibView? = null): XibView {
            val topTemplateResolver = templateForElement(element)
            val view = XibView()
            val p = replacements.getParentRule(element)
            view.type = p?.xibName ?: "view"
            view.customClass = p?.xibCustomView
            view.customModule = p?.xibCustomModule
            p?.xibProperties?.let { view.properties += it.mapValues { it.value.resolve(topTemplateResolver) } }
            p?.xibCustomProperties?.let { view.userDefinedRuntimeAttributes += it.mapValues { it.value.resolve(topTemplateResolver)} }
            p?.xibAttributes?.let { view.attributes += it.mapValues { topTemplateResolver(it.value) } }
            p?.xibConstraints?.let { view.constraints += it.map { it.resolve(topTemplateResolver) } }
            for(att in element.parts) {
                val attResolver = templateForElement(element, att)
                val a = replacements.getAttrRuleSequence(
                    att = att,
                    resolver = attResolver
                ).firstOrNull()

                a?.xibProperties?.let {
                    for((key, value) in it){
                        view.properties[key] = value.resolve(attResolver, view.properties[key] ?: XibNode())
                    }
                }
                a?.xibCustomProperties?.let {
                    for((key, value) in it){
                        view.userDefinedRuntimeAttributes[key] = value.resolve(attResolver, view.userDefinedRuntimeAttributes[key] ?: XibUDNode())
                    }
                }
                a?.xibAttributes?.let {
                    for((key, value) in it){
                        view.attributes[key] = attResolver(value)
                    }
                }
                a?.xibConstraints?.let {
                    val resultsToMerge = it.map { it.resolve(attResolver) }
                    view.constraints += resultsToMerge
                }
            }
            for(child in element.children){
                view.subviews.add(translate(child, view))
            }
            return view
        }
        val view = translate(xml)
        return XibDocument(
            owner = owner,
            view = view,
            resources = resources.values.toList()
        )
    }

    fun Replacements.getParentRule(node: XmlNode): TypeReplacement? = (types[node.name]
        ?: types["android.widget." + node.name]
        ?: types["android.view." + node.name])?.firstOrNull()

    fun Replacements.getAttrRuleSequence(att: XmlNode.Attribute, resolver: (Template)->String): Sequence<AttributeReplacement> {
        val type: AttributeReplacement.AndroidXmlValueType = when {
            att.value.startsWith("@drawable/") || att.value.startsWith("@android:drawable/") -> AttributeReplacement.AndroidXmlValueType.DRAWABLE
            att.value.startsWith("@color/") || att.value.startsWith("@android:color/") -> AttributeReplacement.AndroidXmlValueType.COLOR
            att.value.startsWith("@dimen/") || att.value.startsWith("@android:dimen/") || att.value.endsWith("p") -> AttributeReplacement.AndroidXmlValueType.DIMENSION
            att.value == "false" || att.value == "true" -> AttributeReplacement.AndroidXmlValueType.BOOLEAN
            att.value.all { it.isDigit() || it == '-' || it == '.' } -> AttributeReplacement.AndroidXmlValueType.NUMBER
            else -> AttributeReplacement.AndroidXmlValueType.ENUM_OR_STRING
        }
        val isSet: Boolean = colors[att.value.removePrefix("@color/")]?.isSet == true
        val isImage: Boolean = images.contains(att.value.substringAfter('/'))
        val t = getParentRule(att.parent) ?: return sequenceOf()
        return attributes[att.type.substringAfter(':')]
            ?.asSequence()
            ?.filter { it.on == null || it.on == t.id }
            ?.filter { it.value == null || it.value == att.value }
            ?.filter { it.valueType == null || it.valueType == type }
            ?.filter { it.valueContains == null || att.value.contains(it.valueContains!!) }
            ?.filter { it.isSet == null || it.isSet == isSet }
            ?.filter { it.isImage == null || it.isImage == isImage }
            ?.filter { it.filters.all { it.satisfied(resolver) } }
            ?: sequenceOf()
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
                        out.appendln("static let ${entry.key}: UIColor = ${entry.value.normal.toUIColor()}")
                    } else {
                        out.appendln("static let ${entry.key}: StateSelector<UIColor> = StateSelector(normal: ${entry.value.normal}, highlighted: ${entry.value.highlighted ?: "nil"}, selected: ${entry.value.selected ?: "nil"}, disabled: ${entry.value.disabled ?: "nil"}, focused: ${entry.value.focused ?: "nil"})")
                    }
                }
                appendln("}")

                appendln("}")
            }
        }
    }
}
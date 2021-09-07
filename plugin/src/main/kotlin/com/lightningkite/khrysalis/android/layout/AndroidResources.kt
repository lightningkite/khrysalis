package com.lightningkite.khrysalis.android.layout

import com.lightningkite.khrysalis.replacements.AttributeReplacement
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsBoolean
import com.lightningkite.khrysalis.xml.children
import com.lightningkite.khrysalis.xml.get
import com.lightningkite.khrysalis.xml.readXml
import com.lightningkite.khrysalis.xml.set
import org.mabb.fontverter.FontVerter
import org.w3c.dom.Element
import org.w3c.dom.Text
import java.io.File

private val whitespaceRegexContent = "\\s+"
private val whitespaceRegex = Regex(whitespaceRegexContent)

class AndroidResources {
    var styles: MutableMap<String, AndroidStyle> = HashMap()
    val colors: MutableMap<String, AndroidColorValue> = HashMap()
    val drawables: MutableMap<String, AndroidDrawableResource> = HashMap()
    val fonts: MutableMap<String, AndroidFont> = HashMap()
    val strings: MutableMap<String, AndroidStringResource> = HashMap()
    val dimensions: MutableMap<String, AndroidDimensionResource> = HashMap()
    val layouts: MutableMap<String, AndroidLayoutResource> = HashMap()

    fun read(value: String): AndroidValue {
        return when {
            value.startsWith('#') -> AndroidColor(value.hashColorToParts())
            value.startsWith("@style/") -> styles[value.substringAfter('/')]!!
            value.startsWith("@layout/") -> layouts[value.substringAfter('/')]!!
            value.startsWith("@font/") -> fonts[value.substringAfter('/')]!!
            value.startsWith("@mipmap/") -> drawables[value.substringAfter('/')]!!
            value.startsWith("@drawable/") -> drawables[value.substringAfter('/')]!!
            value.startsWith("@color/") -> colors[value.substringAfter('/')]!!
            value.startsWith("@string/") -> strings[value.substringAfter('/')]!!
            value.startsWith("@dimen/") -> dimensions[value.substringAfter('/')]!!
            value.endsWith("dp") -> AndroidDimension(
                Measurement(
                    number = value.filter { it.isDigit() || it == '.' }.toDouble(),
                    unit = MeasurementUnit.DP
                )
            )
            value.endsWith("dip") -> AndroidDimension(
                Measurement(
                    number = value.filter { it.isDigit() || it == '.' }.toDouble(),
                    unit = MeasurementUnit.DP
                )
            )
            value.endsWith("sp") -> AndroidDimension(
                Measurement(
                    number = value.filter { it.isDigit() || it == '.' }.toDouble(),
                    unit = MeasurementUnit.SP
                )
            )
            value.endsWith("sip") -> AndroidDimension(
                Measurement(
                    number = value.filter { it.isDigit() || it == '.' }.toDouble(),
                    unit = MeasurementUnit.SP
                )
            )
            value.endsWith("px") -> AndroidDimension(
                Measurement(
                    number = value.filter { it.isDigit() || it == '.' }.toDouble(),
                    unit = MeasurementUnit.PX
                )
            )
            value.toDoubleOrNull() != null -> AndroidNumber(value.toDouble())
            else -> AndroidString(value)
        }
    }

    fun parse(androidResourcesDirectory: File) {
        getFonts(androidResourcesDirectory.resolve("font"))
        getStrings(androidResourcesDirectory.resolve("values/strings.xml"))
        getDimensions(androidResourcesDirectory.resolve("values/dimens.xml"))
        getColors(androidResourcesDirectory.resolve("values/colors.xml"))
        androidResourcesDirectory.resolve("color").listFiles()?.forEach {
            getStateColor(it)
        }
        getDrawables(androidResourcesDirectory)
        getStyles(androidResourcesDirectory.resolve("values/styles.xml"))
        getStyles(androidResourcesDirectory.resolve("values/themes.xml"))
    }

    private fun getStyles(file: File) {
        if(!file.exists()) return
        file.readXml().documentElement.children
            .mapNotNull { it as? Element }
            .forEach {
                styles[it["name"]] = AndroidStyle(
                    name = it["name"],
                    map = it.children
                        .mapNotNull { it as? Element }
                        .filter { it.tagName == "style" }
                        .associate {
                            it["name"] to it.children
                                .filter { it is Text }
                                .joinToString { it.textContent }
                                .trim()
                        }
                )
            }
    }

    private fun getDrawables(androidResourcesDirectory: File) {
        if(!androidResourcesDirectory.exists()) return
        val tempDrawables = HashMap<String, HashMap<String, File>>()
        androidResourcesDirectory.listFiles()!!
            .filter { it.name.startsWith("drawable") }
            .forEach { base ->
                val typeName = base.name.substringAfter("drawable-", "")
                for (file in base.listFiles()!!) {
                    tempDrawables.getOrPut(file.nameWithoutExtension) { HashMap() }[typeName] = file
                }
            }
        tempDrawables.forEach { drawables[it.key] = AndroidDrawableResource(it.key, it.value) }
    }

    private fun getStrings(file: File) {
        if (!file.exists()) return
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "string" }
            .forEach {
                val name = it.allAttributes["name"]!!
                strings[name] = AndroidStringResource(
                    name = name,
                    value = it.element.textContent.replace(whitespaceRegex, " ")
                        .replace("\\n", "\n")
                        .replace("\\t", "\t")
                        .replace("\\'", "\'")
                        .replace("\\\"", "\"")
                        .trim()
                )
            }
    }

    private fun getFonts(folder: File) {
        if (!folder.exists()) return
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
                    val iosFont = AndroidFont(
                        family = font.properties.family.filter { it in ' '..'~' },
                        name = font.name.filter { it in '!'..'~' },
                        file = file
                    )
                    println("Found font $iosFont")
                    fonts[file.nameWithoutExtension] = iosFont
                } catch (e: Exception) {
                    println("Font read failed for $file")
                    e.printStackTrace()
                    fonts[file.nameWithoutExtension] = AndroidFont(
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

    private fun getDimensions(file: File) {
        if (!file.exists()) return
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "dimen" }
            .forEach {
                val raw = it.element.textContent
                val name = it.allAttributes["name"]!!
                dimensions[name] = AndroidDimensionResource(
                    name = name,
                    measurement = Measurement(
                        number = raw.filter { it.isDigit() || it == '.' }.toDouble(),
                        unit = raw.filter { it.isLetter() }.toLowerCase().let {
                            when (it) {
                                "px" -> MeasurementUnit.PX
                                "dp", "dip" -> MeasurementUnit.DP
                                "sp", "sip" -> MeasurementUnit.SP
                                else -> MeasurementUnit.PX
                            }
                        })
                )
            }
    }

    private fun getColors(file: File) {
        if (!file.exists()) return
        val colorsToProcess = ArrayList<Pair<String, String>>()
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "color" }
            .forEach {
                val raw = it.element.textContent
                val name = (it.allAttributes["name"] ?: "noname")
                when {
                    raw.startsWith("@color/") -> {
                        val colorName = raw.removePrefix("@color/")
                        colorsToProcess.add(name to colorName)
                    }
                    raw.startsWith("@android:color/") -> {
                        val colorName = raw.removePrefix("@android:color/")
                        colorsToProcess.add(name to colorName)
                    }
                    raw.startsWith("#") -> {
                        this.colors[name] = AndroidColorResource(
                            name = name,
                            value = raw.hashColorToParts()
                        )
                    }
                    else -> {
                    }
                }
            }
        while (colorsToProcess.isNotEmpty()) {
            val popped = colorsToProcess.removeAt(0)
            this.colors[popped.second]?.let {
                this.colors[popped.first] = it
            } ?: colorsToProcess.find { it.first == popped.second }?.let {
                colorsToProcess.add(popped.first to it.second)
            }
        }
    }

    private fun getStateColor(file: File) {
        if (!file.exists()) return
        var normal: AndroidColorValue = AndroidColor(ColorInParts.transparent)
        var selected: AndroidColorValue? = null
        var highlighted: AndroidColorValue? = null
        var disabled: AndroidColorValue? = null
        var focused: AndroidColorValue? = null
        XmlNode.read(file, mapOf())
            .children
            .asSequence()
            .filter { it.name == "item" }
            .forEach { subnode ->
                val raw = subnode.allAttributes["android:color"] ?: ""
                val c: AndroidColorValue? = when {
                    raw.startsWith("@color/") -> {
                        val colorName = raw.removePrefix("@color/")
                        colors[colorName]
                    }
                    raw.startsWith("@android:color/") -> {
                        val colorName = raw.removePrefix("@android:color/")
                        colors[colorName]
                    }
                    raw.startsWith("#") -> AndroidColor(raw.hashColorToParts())
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
        colors[file.nameWithoutExtension] = AndroidColorStateResource(
            name = file.nameWithoutExtension, colors = StateSelector(
                normal = normal,
                selected = selected,
                highlighted = highlighted,
                disabled = disabled,
                focused = focused
            )
        )
    }
}
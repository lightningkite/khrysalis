package com.lightningkite.khrysalis.ios.layout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.ios.swift.retabSwift
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import java.io.File
import com.lightningkite.khrysalis.utils.XmlNode

fun convertLayoutsToSwift(
    androidFolder: File,
    iosFolder: File,
    converter: LayoutConverter = LayoutConverter.normal
) {

    val styles = androidFolder.resolve("src/main/res/values/styles.xml").readXMLStyles()
    iosFolder.resolve("swiftResources/layouts").apply {
        deleteRecursively()
        mkdirs()
    }

    //Load equivalents
    iosFolder.parentFile.walkTopDown()
        .filter {
            it.name.endsWith(".ts.yaml") || it.name.endsWith(".ts.yml")
        }
        .forEach { actualFile ->
            try {
                converter.replacements += actualFile
            } catch (t: Throwable) {
                println("Failed to parse equivalents for $actualFile:")
                t.printStackTrace()
            }
        }

    val androidFiles = jacksonObjectMapper().readValue<Map<String, AndroidLayoutFile>>(
        androidFolder.resolve("build/layout/summary.json")
    )

    for((name, layout) in androidFiles){
        log("Converting layout ${layout.fileName}.xml")
        val output = translateLayoutXml(layout, styles, converter).retabSwift()
        iosFolder.resolve("swiftResources/layouts").resolve(layout.fileName.camelCase().capitalize() + "Xml.swift").also{
            it.parentFile.mkdirs()
        }.writeTextIfDifferent(output)
    }
}

private data class IntermediateStyle(val parent: String? = null, val parts: Map<String, String> = mapOf())

fun File.readXMLStyles(): Map<String, Map<String, String>> {
    if(!this.exists()) {
        println("WARNING: Could not find styles file at '${this}'!")
        return mapOf()
    }
    return XmlNode.read(this, mapOf(), null)
        .children
        .asSequence()
        .filter { it.name == "style" }
        .associate {
            val name = (it.allAttributes["name"] ?: "noname")
            val map = it.children.associate {
                (it.allAttributes["name"] ?: "noname") to it.element.textContent
            }
            val parent = it.allAttributes["parent"]?.removePrefix("@style/") ?: if(name != "AppTheme") "AppTheme" else null
            name to IntermediateStyle(parent, map)
        }
        .let {
            it.mapValues { entry ->
                val complete = HashMap<String, String>()
                var current: IntermediateStyle? = entry.value
                while(current != null) {
                    for((key, value) in current.parts) {
                        if(!complete.containsKey(key)) {
                            complete[key] = value
                        }
                    }
                    val next = current.parent?.let { p -> it[p] }
                    if(next == current) break
                    current = next
                }
                complete
            }
        }
}

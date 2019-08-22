package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.log
import com.lightningkite.kwift.swift.retabSwift
import com.lightningkite.kwift.utils.camelCase
import java.io.File

fun createAndroidLayoutClasses(
    resourcesFolder: File,
    applicationPackage: String,
    outputFolder: File
) {
    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()
    val packageName = outputFolder.toString().substringAfter("src/main/").substringAfter('/').replace('/', '.')

    outputFolder.deleteRecursively()
    File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            log(item.toString())
            val output = item.translateLayoutXmlAndroid(styles, packageName, applicationPackage)
            File(outputFolder, item.nameWithoutExtension.camelCase().capitalize() + "Xml.kt").also{
                it.parentFile.mkdirs()
            }.writeText(output)
        }
}

data class AndroidIdHook(
    val name: String,
    val type: String,
    val resourceId: String
)

fun File.translateLayoutXmlAndroid(styles: Styles, packageName: String, applicationPackage: String): String {

    val node = XmlNode.read(this, styles)
    val fileName = this.nameWithoutExtension.camelCase().capitalize()
    val bindings = ArrayList<AndroidIdHook>()

    fun addBindings(node: XmlNode){
        node.attributes["android:id"]?.let { raw ->
            bindings.add(AndroidIdHook(
                name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase(),
                type = node.name,
                resourceId = raw.removePrefix("@+id/").removePrefix("@id/")
            ))
        }
        node.children.forEach {
            addBindings(it)
        }
    }
    addBindings(node)

    return """
    |//
    |// ${fileName}Xml.swift
    |// Created by Kwift XML Android
    |//
    |package $packageName
    |
    |import android.widget.*
    |import android.view.*
    |import com.lightningkite.kwift.views.actual.*
    |import com.lightningkite.kwift.views.shared.*
    |import $applicationPackage.R
    |
    |class ${fileName}Xml {
    |
    |    ${bindings.joinToString("\n|    ") { it.run { "lateinit var $name: $type" } }}
    |
    |    fun setup(dependency: ViewDependency): View {
    |        val view = LayoutInflater.from(dependency.context).inflate(R.layout.$nameWithoutExtension, null, false)
    |        ${bindings.joinToString("\n|        ") { it.run { "$name = view.findViewById<$type>(R.id.$resourceId)" } }}
    |        return view
    |    }
    |}
    """.trimMargin("|")
}

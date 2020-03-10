package com.lightningkite.khrysalis.layout

import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsGravityKotlin
import com.lightningkite.khrysalis.utils.attributeAsStringKotlin
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File

fun createAndroidLayoutClasses(androidFolder: File, applicationPackage: String) = createAndroidLayoutClasses(
    resourcesFolder = androidFolder.resolve("src/main/res"),
    applicationPackage = applicationPackage,
    outputFolder = androidFolder.resolve("src/main/java/${applicationPackage.replace('.', '/')}/layouts")
)

fun createAndroidLayoutClasses(
    resourcesFolder: File,
    applicationPackage: String,
    outputFolder: File
) {
    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()
    val packageName =
        outputFolder.absolutePath.replace('\\', '/').substringAfter("src/main/").substringAfter('/').replace('/', '.')

    outputFolder.deleteRecursively()
    File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            log(item.toString())
            val output = item.translateLayoutXmlAndroid(styles, packageName, applicationPackage)
            File(outputFolder, item.nameWithoutExtension.camelCase().capitalize() + "Xml.kt").also {
                it.parentFile.mkdirs()
            }.writeText(output)
        }
}

private data class AndroidIdHook(
    val name: String,
    val type: String,
    val resourceId: String
)

private data class AndroidDelegateHook(
    val name: String,
    val type: String,
    val resourceId: String
)

private data class AndroidSubLayout(
    val name: String,
    val resourceId: String,
    val layoutXmlClass: String
)

private fun File.translateLayoutXmlAndroid(styles: Styles, packageName: String, applicationPackage: String): String {

    val node = XmlNode.read(this, styles)
    val fileName = this.nameWithoutExtension.camelCase().capitalize()
    val bindings = ArrayList<AndroidIdHook>()
    val delegateBindings = ArrayList<AndroidDelegateHook>()
    val sublayouts = ArrayList<AndroidSubLayout>()
    val emitCurse = ArrayList<String>()

    fun addBindings(node: XmlNode) {
        node.attributes["android:id"]?.let { raw ->
            val id = raw.removePrefix("@+id/").removePrefix("@id/")
            val camelCasedId = id.camelCase()
            if (node.name == "include") {
                val layout = node.attributes["layout"]!!.removePrefix("@layout/")
                sublayouts.add(
                    AndroidSubLayout(
                        name = camelCasedId,
                        resourceId = id,
                        layoutXmlClass = layout.camelCase().capitalize() + "Xml"
                    )
                )
            } else {
                val name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase()
                bindings.add(
                    AndroidIdHook(
                        name = name,
                        type = node.name,
                        resourceId = raw.removePrefix("@+id/").removePrefix("@id/")
                    )
                )
                node.attributeAsGravityKotlin("tools:systemEdges")?.let {
                    emitCurse.add("$name.safeInsets($it)")
                }
                node.attributeAsGravityKotlin("tools:systemEdgesSizing")?.let {
                    emitCurse.add("$name.safeInsetsSizing($it)")
                }
                (node.attributes["app:delegateClass"] ?: node.attributes["delegateClass"])?.let {
                    delegateBindings.add(
                        AndroidDelegateHook(
                            name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase(),
                            type = it,
                            resourceId = raw.removePrefix("@+id/").removePrefix("@id/")
                        )
                    )
                }
            }
        }
        node.children.forEach {
            addBindings(it)
        }
    }
    addBindings(node)

    return """
    |//
    |// ${fileName}Xml.swift
    |// Created by Khrysalis XML Android
    |//
    |package $packageName
    |
    |import android.widget.*
    |import android.view.*
    |import com.lightningkite.khrysalis.views.*
    |import $applicationPackage.R
    |
    |class ${fileName}Xml {
    |
    |    ${bindings.joinToString("\n|    ") { it.run { "lateinit var $name: $type" } }}
    |    ${delegateBindings.joinToString("\n|    ") { it.run { "lateinit var ${name}Delegate: $type" } }}
    |    ${sublayouts.joinToString("\n|    ") { it.run { "lateinit var $name: $layoutXmlClass" } }}
    |    lateinit var xmlRoot: View
    |
    |    fun setup(dependency: ViewDependency): View {
    |        val view = LayoutInflater.from(dependency.context).inflate(R.layout.$nameWithoutExtension, null, false)
    |        return setup(view)
    |    }
    |    fun setup(view: View): View {
    |        xmlRoot = view
    |        ${bindings.joinToString("\n|        ") { it.run { "$name = view.findViewById<$type>(R.id.$resourceId)" } }}
    |        ${delegateBindings.joinToString("\n|        ") { it.run { "${name}Delegate = view.findViewById<CustomView>(R.id.$resourceId).delegate as $type" } }}
    |        ${sublayouts.joinToString("\n|        ") { it.run { "$name = $layoutXmlClass().apply{ setup(view.findViewById<View>(R.id.$resourceId)) }" } }}
    |        ${emitCurse.joinToString("\n|        ")}
    |        return view
    |    }
    |}
    """.trimMargin("|")
}

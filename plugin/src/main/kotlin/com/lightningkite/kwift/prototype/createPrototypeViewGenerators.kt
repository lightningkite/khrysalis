package com.lightningkite.kwift.prototype

import com.lightningkite.kwift.layout.readXMLStyles
import com.lightningkite.kwift.log
import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsString
import com.lightningkite.kwift.utils.camelCase
import java.io.File


fun createPrototypeViewGenerators(androidFolder: File, applicationPackage: String) = createPrototypeViewGenerators(
    resourcesFolder = androidFolder.resolve("src/main/res"),
    applicationPackage = applicationPackage,
    outputFolder = androidFolder.resolve("src/main/java/${applicationPackage.replace('.', '/')}/shared/vg")
)

internal fun createPrototypeViewGenerators(
    resourcesFolder: File,
    applicationPackage: String,
    outputFolder: File
) {
    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()
    val packageName = outputFolder.toString().substringAfter("src/main/").substringAfter('/').replace('/', '.')
    val nodes = HashMap<String, ViewNode>()
    val files = File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .filter { !it.name.contains("component") }

    //Gather graph information
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = ViewNode(fileName)
        node.gather(XmlNode.read(item, styles))
        nodes[fileName] = node
    }

    for ((key, value) in nodes) {
        println("$key connects to ${value.instantiates.joinToString()}")
    }

    //Emit graph
    groupedGraph(outputFolder, nodes)
    sortedGraph(outputFolder, nodes)


    fun File.createPrototypeVG(into: Appendable) {

        val node = XmlNode.read(this, mapOf())
        val fileName = this.nameWithoutExtension.camelCase().capitalize()

        with(into) {
            appendln("""//""")
            appendln("""// ${fileName}VG.swift""")
            appendln("""// Created by Kwift Prototype Generator""")
            appendln("""//""")
            appendln("""package $packageName""")
            appendln("""""")
            appendln("""import android.widget.*""")
            appendln("""import android.view.*""")
            appendln("""import com.lightningkite.kwift.actual.*""")
            appendln("""import com.lightningkite.kwift.shared.*""")
            appendln("""import com.lightningkite.kwift.views.actual.*""")
            appendln("""import com.lightningkite.kwift.views.shared.*""")
            appendln("""import com.lightningkite.kwift.observable.actual.*""")
            appendln("""import com.lightningkite.kwift.observable.shared.*""")
            appendln("""import $applicationPackage.R""")
            appendln("""""")
            appendln("""class ${fileName}VG(stack: ObservableStack<ViewGenerator>) : ViewGenerator() {""")
            appendln("""    val stack: ObservableStack<ViewGenerator>? by weak(stack)""")
            appendln("""    override val title: String get() = "${fileName}"""")
            appendln("""    """)
            appendln("""    override fun generate(dependency: ViewDependency): View {""")
            appendln("""        val xml = ${fileName}Xml()""")
            appendln("""        val view = xml.setup(dependency)""")

            with(into) {
                @Suppress("ConvertToStringTemplate")
                fun handleNode(node: XmlNode) {

                    val view = node.attributes["android:id"]?.removePrefix("@+id/")?.camelCase()
                    if (view != null) {
                        node.attributeAsString("tools:text")?.let {
                            appendln("        xml.$view.text = $it")
                        }
                        node.attributeAsString("tools:visibility")?.let {
                            when (it) {
                                "gone" -> appendln("        xml.$view.visibility = View.GONE")
                                "invisible" -> appendln("        xml.$view.visibility = View.INVISIBLE")
                                "visible" -> appendln("        xml.$view.visibility = View.VISIBLE")
                                else -> {
                                }
                            }
                        }
                        //Pop
                        //Push
                        //Substack
                        //Swap
                        node.attributes["tools:listitem"]?.let {
                            val subVg = it.removePrefix("@layout/").camelCase().capitalize() + "VG"
                            appendln("        xml.$view.bind(")
                            appendln("            data = ConstantObservableProperty(listOf(1, 2, 3, 4)),")
                            appendln("            defaultValue = 1,")
                            appendln("            makeView = { obs ->")
                            appendln("                val sub = $subVg()")
                            appendln("                return@bind sub.generate(dependency)")
                            appendln("            }")
                            appendln("        )")
                        }
                        appendln("        ")
                    } else {
                        if (node.attributes.keys.any { it.startsWith("tools:") }) {
                            println("WARNING: ${this@createPrototypeVG.name}: Element type ${node.name} has tools but no id")
                        }
                    }

                    node.children.forEach {
                        handleNode(it)
                    }
                }
                handleNode(node)
            }

            appendln("""        return view""")
            appendln("""    }""")
            appendln("""}""")
        }
    }


}

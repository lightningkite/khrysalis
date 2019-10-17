package com.lightningkite.kwift.flow

import com.lightningkite.kwift.layout.Styles
import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsString
import com.lightningkite.kwift.utils.camelCase
import com.lightningkite.kwift.utils.forEachBetween
import java.io.File
import java.lang.IllegalArgumentException

internal val oldWarning = "Any changes made to this file will be overridden unless this comment is removed."

internal fun createPrototypeVG(
    styles: Styles,
    viewName: String,
    xml: File,
    target: File,
    viewNodeMap: Map<String, ViewNode>,
    viewNode: ViewNode,
    packageName: String,
    applicationPackage: String
) {
    if (target.exists() && target.useLines { it.any { it.contains(oldWarning) } }) {
        //full override
        target.writeText(generateFile(xml, viewNodeMap, viewName, packageName, applicationPackage, viewNode, styles))
    } else if (target.exists() && target.useLines { it.any { it.contains(CodeSection.overwriteMarker) } }) {
        //partial override
        val generated = CodeSection.read(
            generateFile(
                xml,
                viewNodeMap,
                viewName,
                packageName,
                applicationPackage,
                viewNode,
                styles
            ).lines()
        )
        val existing = CodeSection.read(target.readLines())
        target.writeText(buildString {
            with(TabWriter(this)) {
                existing.mergeOverride(generated).forEach {
                    it.writeWhole(this)
                }
            }
        })
    } else if (!target.exists()) {
        //new file
        target.writeText(generateFile(xml, viewNodeMap, viewName, packageName, applicationPackage, viewNode, styles))
    }

}

private fun generateFile(
    xml: File,
    viewNodeMap: Map<String, ViewNode>,
    viewName: String,
    packageName: String,
    applicationPackage: String,
    viewNode: ViewNode,
    styles: Styles
): String {
    val node = XmlNode.read(xml, mapOf())

    fun makeView(otherViewNode: ViewNode, forStack: String?): String {
        val totalProvides = viewNode.totalRequires(viewNodeMap) + viewNode.provides
        return otherViewNode
            .totalRequires(viewNodeMap)
            .sortedBy { it.name }
            .filter {
                val included = it in totalProvides || it.name == "stack"
                if (!included && it.default == null) throw IllegalArgumentException("Cannot provide arg ${it.name} for ${otherViewNode.name} in ${viewNode.name}")
                included
            }
            .joinToString(
                ", ",
                "${otherViewNode.name}VG(",
                ")"
            ) { arg ->
                val myName = if (arg.name == "stack") "this.$forStack" else "this." + arg.name
                arg.name + " = " + myName
            }
    }

    val into = StringBuilder()

    with(TabWriter(into)) {
        fun handleNodeClick(
            node: XmlNode,
            view: String?
        ) {
            node.attributes["tools:print"]?.let {
                println(it)
            }
            node.attributes[ViewNode.attributePush]?.let {
                val otherViewNode =
                    viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                val makeView = makeView(otherViewNode, stackName)
                line("$view.onClick { this.$stackName.push($makeView) }")
            }
            node.attributes[ViewNode.attributeSwap]?.let {
                val otherViewNode =
                    viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                val makeView = makeView(otherViewNode, stackName)
                line("$view.onClick { this.$stackName.swap($makeView) }")
            }
            node.attributes[ViewNode.attributeReset]?.let {
                val otherViewNode =
                    viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                val makeView = makeView(otherViewNode, stackName)
                line("$view.onClick { this.$stackName.reset($makeView) }")
            }
            node.attributes[ViewNode.attributePop]?.let {
                val stackNames = node.attributes[ViewNode.attributeOnStack]?.split(';') ?: listOf("stack")
                if (stackNames.size == 1) {
                    line("$view.onClick { this.${stackNames.first()}.pop() }")
                } else {
                    line("$view.onClick { ")
                    tab {
                        startLine()
                        stackNames.forEachBetween(
                            forItem = {
                                direct.append("if(this.$it.pop()) {}")
                            },
                            between = {
                                direct.append(" else ")
                            }
                        )
                    }
                    line("}")
                }
            }
            node.attributes[ViewNode.attributeDismiss]?.let {
                val stackNames = node.attributes[ViewNode.attributeOnStack]?.split(';') ?: listOf("stack")
                if (stackNames.size == 1) {
                    line("$view.onClick { this.${stackNames.first()}.dismiss() }")
                } else {
                    line("$view.onClick { ")
                    tab {
                        startLine()
                        stackNames.forEachBetween(
                            forItem = {
                                direct.append("if(self.$it.dismiss()) {}")
                            },
                            between = {
                                direct.append(" else ")
                            }
                        )
                    }
                    line("}")
                }
            }
        }

        var inits = ArrayList<() -> Unit>()

        line("//")
        line("// ${viewName}VG.swift")
        line("// Created by Kwift Prototype Generator")
        line("// Sections of this file can be replaces if the marker, '${CodeSection.overwriteMarker}', is left in place.")
        line("//")
        line("package $packageName")
        line("")
        line("${CodeSection.sectionMarker} Imports")
        line("")
        line("import android.widget.*")
        line("import android.view.*")
        line("import com.lightningkite.kwift.actual.*")
        line("import com.lightningkite.kwift.shared.*")
        line("import com.lightningkite.kwift.views.actual.*")
        line("import com.lightningkite.kwift.views.shared.*")
        line("import com.lightningkite.kwift.observables.actual.*")
        line("import com.lightningkite.kwift.observables.shared.*")
        line("import $applicationPackage.R")
        line("import $applicationPackage.layouts.*")
        line("")
        line("${CodeSection.sectionMarker} Name ${CodeSection.overwriteMarker}")
        line("""@Suppress("NAME_SHADOWING")""")
        line("class ${viewName}VG(")
        tab {
            line("${CodeSection.sectionMarker} Dependencies ${CodeSection.overwriteMarker}")
            val things = (viewNode.totalRequires(viewNodeMap).sortedBy { it.name })
            things.forEachIndexed { index, it ->
                if (it.type.contains("VG") || it.type.contains("ViewGenerator")) {
                    line("@unowned val $it" + (if (index == things.lastIndex) "" else ","))
                } else if (it.type.contains("->")) {
                    line("val ${it.name}: @escaping() ${it.kotlinType}" + (if (index == things.lastIndex) "" else ","))
                } else {
                    line("val $it" + (if (index == things.lastIndex) "" else ","))
                }
            }
            line("${CodeSection.sectionMarker} Extends ${CodeSection.overwriteMarker}")
        }
        line(") : ViewGenerator() {")
        tab {
            line()
            viewNode.provides.sortedBy { it.name }.forEach {
                line("${CodeSection.sectionMarker} Provides ${it.name} ${CodeSection.overwriteMarker}")
                line(
                    """val ${it.name}: ${it.kotlinType} = ${it.construct(
                        viewNode,
                        viewNodeMap
                    )}"""
                )
            }
            line("")
            line("${CodeSection.sectionMarker} Title ${CodeSection.overwriteMarker}")
            line("""override val title: String get() = "${viewName}"""")
            line("")
            line("${CodeSection.sectionMarker} Generate Start ${CodeSection.overwriteMarker}")
            line("""override fun generate(dependency: ViewDependency): View {""")
            tab {
                line("val xml = ${viewName}Xml()")
                line("val view = xml.setup(dependency)")

                fun handleNode(node: XmlNode, prefix: String) {

                    val view = node.attributes["android:id"]?.removePrefix("@+id/")?.camelCase()?.let {
                        if (node.name == "include") it + ".xmlRoot"
                        else it
                    }?.let { prefix + it }
                    if (view != null) {
                        line()
                        line("${CodeSection.sectionMarker} Set Up ${view} ${CodeSection.overwriteMarker}")
                        node.attributes["tools:text"]?.let {
                            if (it.startsWith("@string")) {
                                line("""$view.bindStringRes(ConstantObservableProperty(R.string.${it.removePrefix("@string/")}))""")
                            } else {
                                line("""$view.bindString(ConstantObservableProperty("${it.replace("$", "\\$")}"))""")
                            }
                        }
                        node.attributes["tools:src"]?.let {
                            if (it.startsWith("@drawable")) {
                                line("""$view.setImageResource(R.drawable.${it.removePrefix("@drawable/")})""")
                            }
                        }
                        node.attributeAsString("tools:visibility")?.let {
                            when (it) {
                                "gone" -> line("$view.visibility = View.GONE")
                                "invisible" -> line("$view.visibility = View.INVISIBLE")
                                "visible" -> line("$view.visibility = View.VISIBLE")
                                else -> {
                                }
                            }
                        }
                        node.attributes["tools:listitem"]?.let {
                            val xmlName = it.removePrefix("@layout/").camelCase().capitalize().plus("Xml")
                            line("$view.bind(")
                            tab {
                                line("data = ConstantObservableProperty(listOf(1, 2, 3, 4)),")
                                line("defaultValue = 1,")
                                line("makeView = label@ { obs ->")
                                tab {
                                    line("${CodeSection.sectionMarker} Make Subview For ${view} ${CodeSection.overwriteMarker}")
                                    line("val cellXml = $xmlName() ")
                                    line("val cellView = cellXml.setup(dependency)")
                                    val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
                                    handleNode(XmlNode.read(file, styles), "cellXml.")
                                    handleNodeClick(node, "cellXml.xmlRoot")
                                    line("${CodeSection.sectionMarker} End Make Subview For ${view} ${CodeSection.overwriteMarker}")
                                    line("return@label cellView")
                                }
                                line("}")
                            }
                            line(")")
                        }
                        handleNodeClick(node, view)
                        node.attributes[ViewNode.attributeStackId]?.let { stackName ->
                            node.attributes[ViewNode.attributeStackDefault]?.let stackDefault@{
                                val otherViewNode =
                                    viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()]
                                        ?: return@stackDefault
                                val makeView = makeView(otherViewNode, stackName)
                                inits.add {
                                    line("${CodeSection.sectionMarker} Set Initial View for ${stackName} ${CodeSection.overwriteMarker}")
                                    line("this.$stackName.reset($makeView)")
                                }
                            }
                            line("$view.bindStack(dependency, ${stackName})")
                        }
                    } else {
                        if (node.attributes.keys.any { it.startsWith("tools:") }) {
                            println("WARNING: ${xml.name}: Element type ${node.name} has tools but no id")
                        }
                    }

                    if (node.name == "include") {
                        val id = node.attributes["android:id"]?.removePrefix("@+id/")?.camelCase()
                        if (id != null) {
                            node.attributes["layout"]?.let {
                                val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
                                handleNode(XmlNode.read(file, styles), "$prefix$id.")
                            }
                        }
                    }
                    node.children.forEach {
                        handleNode(it, prefix)
                    }
                }
                handleNode(node, "xml.")
                line("")
                line("${CodeSection.sectionMarker} Generate End ${CodeSection.overwriteMarker}")
                line("")
                line("return view")
            }
            line("}")
            line("")
            line("${CodeSection.sectionMarker} Init")
            line("")
            line("init {")
            tab {
                inits.forEach { it() }
            }
            line("${CodeSection.sectionMarker} Init End")
            line("}")
            line("")
            line("${CodeSection.sectionMarker} Body End")
        }
        line("}")
    }
    return into.toString()
}

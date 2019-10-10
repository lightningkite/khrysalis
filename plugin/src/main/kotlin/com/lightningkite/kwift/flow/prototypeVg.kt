package com.lightningkite.kwift.flow

import com.lightningkite.kwift.layout.Styles
import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsString
import com.lightningkite.kwift.utils.camelCase
import com.lightningkite.kwift.utils.forEachBetween
import java.io.File

internal val warning = "Any changes made to this file will be overridden unless this comment is removed."

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
    if (target.exists() && target.useLines { it.none { it.contains(warning) } }) {
        println("Skipping $viewName")
        return
    }
    val node = XmlNode.read(xml, mapOf())

    fun makeView(otherViewNode: ViewNode, forStack: String?): String {
        return (listOf(ViewNode.stack) + otherViewNode.totalRequires(viewNodeMap)).joinToString(
            ", ",
            "${otherViewNode.name}VG(",
            ")"
        ) {
            val myName = if (it.name == "stack") forStack ?: it.name else it.name
            it.name + " = this." + myName
        }
    }

    target.bufferedWriter().use { into ->
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
                    line("$view.onClick(captureWeak(this){ self -> self.$stackName.push($makeView) })")
                }
                node.attributes[ViewNode.attributeSwap]?.let {
                    val otherViewNode =
                        viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                    val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                    val makeView = makeView(otherViewNode, stackName)
                    line("$view.onClick(captureWeak(this){ self -> self.$stackName.swap($makeView) })")
                }
                node.attributes[ViewNode.attributeReset]?.let {
                    val otherViewNode =
                        viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                    val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                    val makeView = makeView(otherViewNode, stackName)
                    line("$view.onClick(captureWeak(this){ self -> self.$stackName.reset($makeView) })")
                }
                node.attributes[ViewNode.attributePop]?.let {
                    val stackNames = node.attributes[ViewNode.attributeOnStack]?.split(';') ?: listOf("stack")
                    if (stackNames.size == 1) {
                        line("$view.onClick(captureWeak(this){ self -> self.${stackNames.first()}.pop() })")
                    } else {
                        line("$view.onClick(captureWeak(this){ self -> ")
                        tab {
                            startLine()
                            stackNames.forEachBetween(
                                forItem = {
                                    direct.append("if(self.$it.pop()) {}")
                                },
                                between = {
                                    direct.append(" else ")
                                }
                            )
                        }
                        line("})")
                    }
                }
            }

            line("//")
            line("// ${viewName}VG.swift")
            line("// Created by Kwift Prototype Generator")
            line("// $warning")
            line("//")
            line("package $packageName")
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
            line("""@Suppress("NAME_SHADOWING")""")
            line("class ${viewName}VG(")
            tab {
                val things = (listOf(ViewNode.stack) + viewNode.totalRequires(viewNodeMap))
                things.forEachIndexed { index, it ->
                    if (it.type.contains("VG") || it.type.contains("ViewGenerator")) {
                        line("@unowned val $it" + (if (index == things.lastIndex) "" else ","))
                    } else {
                        line("val $it" + (if (index == things.lastIndex) "" else ","))
                    }
                }
            }
            line(") : ViewGenerator() {")
            tab {
                line()
                viewNode.provides.forEach {
                    line(
                        """val ${it.name}: ${it.kotlinType} = ${ViewVar.construct(
                            viewNode,
                            viewNodeMap,
                            it.kotlinType
                        )}"""
                    )
                }
                line("")
                line("""override val title: String get() = "${viewName}"""")
                line("")
                line("""override fun generate(dependency: ViewDependency): View {""")
                tab {
                    line("val xml = ${viewName}Xml()")
                    line("val view = xml.setup(dependency)")
                    line("")

                    fun handleNode(node: XmlNode, prefix: String) {

                        val view = node.attributes["android:id"]?.removePrefix("@+id/")?.camelCase()?.let {
                            if (node.name == "include") it + ".xmlRoot"
                            else it
                        }?.let { prefix + it }
                        if (view != null) {
                            node.attributes["tools:text"]?.let {
                                if (it.startsWith("@string")) {
                                    line("""$view.textResource = R.string.${it.removePrefix("@string/")}""")
                                } else {
                                    line("""$view.textString = "$it"""")
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
                                    line("makeView = label@ @unownedSelf { obs ->")
                                    tab {
                                        line("val cellXml = $xmlName() ")
                                        line("val cellView = cellXml.setup(dependency)")
                                        val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
                                        handleNode(XmlNode.read(file, styles), "cellXml.")
                                        handleNodeClick(node, "cellXml.xmlRoot")
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
                                    line("this.$stackName.reset($makeView)")
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
                    line("//region View Setup")
                    line("")
                    line("//endregion View Setup")
                    line("")
                    line("return view")
                }
                line("}")
                line("")
                line("//region View Functions")
                line("")
                line("//endregion View Functions")
                line("")
            }
            line("}")
        }
    }
}

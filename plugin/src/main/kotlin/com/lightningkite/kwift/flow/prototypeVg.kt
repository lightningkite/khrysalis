package com.lightningkite.kwift.flow

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsString
import com.lightningkite.kwift.utils.camelCase
import com.lightningkite.kwift.utils.forEachBetween
import java.io.File

private val warning = "Any changes made to this file will be overridden unless this comment is removed. "

internal fun createPrototypeVG(
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
            it.name + " = self." + myName
        }
    }

    target.bufferedWriter().use { into ->
        with(into) {
            appendln("""//""")
            appendln("""// ${viewName}VG.swift""")
            appendln("""// Created by Kwift Prototype Generator""")
            appendln("""// $warning""")
            appendln("""//""")
            appendln("""package $packageName""")
            appendln("""""")
            appendln("""import android.widget.*""")
            appendln("""import android.view.*""")
            appendln("""import com.lightningkite.kwift.actual.*""")
            appendln("""import com.lightningkite.kwift.shared.*""")
            appendln("""import com.lightningkite.kwift.views.actual.*""")
            appendln("""import com.lightningkite.kwift.views.shared.*""")
            appendln("""import com.lightningkite.kwift.observables.actual.*""")
            appendln("""import com.lightningkite.kwift.observables.shared.*""")
            appendln("""import $applicationPackage.R""")
            appendln("""import $applicationPackage.layouts.*""")
            appendln("""""")
            appendln("class ${viewName}VG(")
            (listOf(ViewNode.stack) + viewNode.totalRequires(viewNodeMap)).forEachBetween(
                forItem = {
                    if (it.type.contains("VG") || it.type.contains("ViewGenerator")) {
                        append("    @unowned val $it")
                    } else {
                        append("    val $it")
                    }
                },
                between = {
                    appendln(",")
                }
            )
            appendln()
            appendln(") : ViewGenerator() {")
            appendln("""    """)
            viewNode.provides.forEach {
                appendln(
                    """    val ${it.name}: ${it.kotlinType} = ${ViewVar.construct(
                        viewNode,
                        viewNodeMap,
                        it.kotlinType
                    )}"""
                )
            }
            appendln("""    """)
            appendln("""    override val title: String get() = "${viewName}"""")
            appendln("""    """)
            appendln("""    override fun generate(dependency: ViewDependency): View {""")
            appendln("""        val xml = ${viewName}Xml()""")
            appendln("""        val view = xml.setup(dependency)""")
            appendln("""        """)

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
                    node.attributes["tools:listitem"]?.let {
                        val otherViewNode = viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()]
                            ?: return@let
                        val makeView = makeView(otherViewNode, null)
                        appendln("        val self by weak(this)")
                        appendln("        xml.$view.bind(")
                        appendln("            data = ConstantObservableProperty(listOf(1, 2, 3, 4)),")
                        appendln("            defaultValue = 1,")
                        appendln("            makeView = { obs ->")
                        appendln("                if(")
                        appendln("                val sub = $makeView")
                        appendln("                return@captureWeak sub.generate(dependency)")
                        appendln("            }")
                        appendln("        )")
                    }
                    node.attributes[ViewNode.attributePush]?.let {
                        val otherViewNode =
                            viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                        val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                        val makeView = makeView(otherViewNode, stackName)
                        appendln("        xml.$view.onClick(captureWeak(this){ self -> self.$stackName.push($makeView) })")
                    }
                    node.attributes[ViewNode.attributeSwap]?.let {
                        val otherViewNode =
                            viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                        val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                        val makeView = makeView(otherViewNode, stackName)
                        appendln("        xml.$view.onClick(captureWeak(this){ self -> self.$stackName.swap($makeView) })")
                    }
                    node.attributes[ViewNode.attributeRoot]?.let {
                        val otherViewNode =
                            viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()] ?: return@let
                        val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                        val makeView = makeView(otherViewNode, stackName)
                        appendln("        xml.$view.onClick(captureWeak(this){ self -> self.$stackName.root($makeView) })")
                    }
                    node.attributes[ViewNode.attributePop]?.let {
                        val stackName = node.attributes[ViewNode.attributeOnStack] ?: "stack"
                        appendln("        xml.$view.onClick(captureWeak(this){ self -> self.$stackName.pop() })")
                    }
                    node.attributes[ViewNode.attributeStackId]?.let {
                        val stackName = node.attributes[ViewNode.attributeOnStack] ?: return@let
                        node.attributes[ViewNode.attributeStackDefault]?.let stackDefault@{
                            val otherViewNode =
                                viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()]
                                    ?: return@stackDefault
                            val makeView = makeView(otherViewNode, stackName)
                            appendln("        self.$stackName.root($makeView) })")
                        }
                        appendln("        xml.$view.bindStack(dependency, ${stackName})")
                    }
                } else {
                    if (node.attributes.keys.any { it.startsWith("tools:") }) {
                        println("WARNING: ${xml.name}: Element type ${node.name} has tools but no id")
                    }
                }

                node.children.forEach {
                    handleNode(it)
                }
            }
            handleNode(node)
            appendln("""        """)
            appendln("""        //region View Setup""")
            appendln("""        """)
            appendln("""        //endregion View Setup""")
            appendln("""        """)
            appendln("""        return view""")
            appendln("""    }""")
            appendln("""    """)
            appendln("""    //region View Functions""")
            appendln("""    """)
            appendln("""    //endregion View Functions""")
            appendln("""    """)
            appendln("""}""")
        }
    }
}

package com.lightningkite.khrysalis.android.layout

import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.typescript.DeclarationManifest
import com.lightningkite.khrysalis.typescript.renderImports
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsBoolean
import com.lightningkite.khrysalis.utils.attributeAsEdgeFlagsKotlin
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File

data class AndroidLayoutFile(
    val name: String,
    val fileName: String,
    val variants: Set<String>,
    val bindings: Map<String, AndroidIdHook>,
    val delegateBindings: Map<String, AndroidDelegateHook>,
    val sublayouts: Map<String, AndroidSubLayout>,
    val emitCurse: Map<String, AndroidAction>
) {
    companion object {
        fun combine(iter: Sequence<AndroidLayoutFile>): AndroidLayoutFile =
            AndroidLayoutFile(
                name = iter.first().name,
                fileName = iter.first().fileName,
                variants = iter.flatMap { it.variants.asSequence() }.toSet(),
                bindings = run {
                    (iter.flatMap { it.bindings.asSequence() }.associate { it.toPair() }).mapValues { (key, value) ->
                        if (iter.all { it.bindings[key] != null }) value
                        else value.copy(optional = true)
                    }
                },
                delegateBindings = run {
                    (iter.flatMap { it.delegateBindings.asSequence() }
                        .associate { it.toPair() }).mapValues { (key, value) ->
                            if (iter.all { it.delegateBindings[key] != null }) value
                            else value.copy(optional = true)
                        }
                },
                sublayouts = run {
                    (iter.flatMap { it.sublayouts.asSequence() }.associate { it.toPair() }).mapValues { (key, value) ->
                        if (iter.all { it.sublayouts[key] != null }) value
                        else value.copy(optional = true)
                    }
                },
                emitCurse = run {
                    (iter.flatMap { it.emitCurse.asSequence() }.associate { it.toPair() }).mapValues { (key, value) ->
                        if (iter.all { it.emitCurse[key] != null }) value
                        else value.copy(optional = true)
                    }
                }
            )

        fun parseAll(folder: File, styles: Styles): Map<String, AndroidLayoutFile> {
            return folder.listFiles()!!.asSequence().filter { it.name.startsWith("layout") }
                .flatMap { it.listFiles()!!.asSequence() }
                .map { it.name }
                .distinct()
                .map { parseSet(folder, it, styles) }
                .associateBy { it.name }
        }

        fun parseSet(folder: File, filename: String, styles: Styles): AndroidLayoutFile {
            return folder.listFiles()!!.asSequence().filter { it.name.startsWith("layout") }
                .map { it.resolve(filename) }
                .filter { it.exists() }
                .map { parse(folder.resolve("layout"), it, it.parentFile.name.substringAfter("layout-", ""), styles) }
                .let { combine(it) }
        }

        fun parse(baseFolder: File, file: File, variant: String, styles: Styles): AndroidLayoutFile {
            val node = XmlNode.read(file, styles, baseFolder)
            val fileName = file.nameWithoutExtension.camelCase().capitalize()
            val bindings = ArrayList<AndroidIdHook>()
            val delegateBindings = ArrayList<AndroidDelegateHook>()
            val sublayouts = ArrayList<AndroidSubLayout>()
            val emitCurse = ArrayList<AndroidAction>()

            fun addBindings(node: XmlNode) {
                if(node.name == "com.google.android.material.tabs.TabItem") {
                    return
                }
                node.allAttributes["android:id"]?.let { raw ->
                    val id = raw.removePrefix("@+id/").removePrefix("@id/")
                    val camelCasedId = id.camelCase()
                    if (node.name == "include") {
                        val layout = node.allAttributes["layout"]!!.removePrefix("@layout/")
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
                        node.attributeAsBoolean("tools:focusAtStartup")?.let { value ->
                            emitCurse.add(
                                AndroidAction(
                                    name,
                                    "focusAtStartup = $value"
                                )
                            )
                        }
                        node.attributeAsEdgeFlagsKotlin("tools:systemEdges")?.let {
                            emitCurse.add(
                                AndroidAction(
                                    name,
                                    "safeInsets($it)"
                                )
                            )
                        }
                        node.attributeAsEdgeFlagsKotlin("tools:systemEdgesSizing")?.let {
                            emitCurse.add(
                                AndroidAction(
                                    name,
                                    "safeInsetsSizing($it)"
                                )
                            )
                        }
                        node.attributeAsEdgeFlagsKotlin("tools:systemEdgesBoth")?.let {
                            emitCurse.add(
                                AndroidAction(
                                    name,
                                    "safeInsetsBoth($it)"
                                )
                            )
                        }
                        (node.allAttributes["app:delegateClass"] ?: node.allAttributes["delegateClass"])?.let {
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
            return AndroidLayoutFile(
                name = fileName,
                fileName = file.nameWithoutExtension,
                variants = if(variant.isNotEmpty()) setOf(variant) else setOf(),
                bindings = bindings.associateBy { it.name },
                delegateBindings = delegateBindings.associateBy { it.name },
                sublayouts = sublayouts.associateBy { it.name },
                emitCurse = emitCurse.associateBy { it.name }
            )
        }
    }

    fun toString(packageName: String, applicationPackage: String): String = """
    |//
    |// ${name}Xml.kt
    |// Created by Khrysalis XML Android
    |//
    |package $packageName
    |
    |import android.widget.*
    |import android.view.*
    |import com.lightningkite.butterfly.views.widget.*
    |import com.lightningkite.butterfly.views.*
    |import com.lightningkite.butterfly.android.*
    |import $applicationPackage.R
    |
    |class ${name}Xml {
    |
    |    ${bindings.values.joinToString("\n|    ") { it.declaration }}
    |    ${delegateBindings.values.joinToString("\n|    ") { it.declaration }}
    |    ${sublayouts.values.joinToString("\n|    ") { it.declaration }}
    |    lateinit var xmlRoot: View
    |
    |    fun setup(dependency: ActivityAccess): View {
    |        val view = LayoutInflater.from(dependency.context).inflate(R.layout.$fileName, null, false)
    |        return setup(view)
    |    }
    |    fun setup(view: View): View {
    |        xmlRoot = view
    |        ${bindings.values.joinToString("\n|        ") { it.initiation }}
    |        ${delegateBindings.values.joinToString("\n|        ") { it.initiation }}
    |        ${sublayouts.values.joinToString("\n|        ") { it.initiation }}
    |        ${emitCurse.values.joinToString("\n|        ") { it.invocation }}
    |        return view
    |    }
    |}
    """.trimMargin("|")

}

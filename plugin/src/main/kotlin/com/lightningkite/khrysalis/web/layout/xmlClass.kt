package com.lightningkite.khrysalis.web.layout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.typescript.DeclarationManifest
import com.lightningkite.khrysalis.typescript.renderImports
import com.lightningkite.khrysalis.typescript.replacements.Replacements
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.safeJsIdentifier
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File

fun convertLayoutsToHtmlXmlClasses(
    projectName: String?,
    packageName: String,
    androidLayoutsSummaryFile: File,
    baseTypescriptFolder: File,
    outputFolder: File
) {
    log("Creating layout classes")
    outputFolder.mkdirs()
    val declarationManifest = DeclarationManifest.load(
        baseTypescriptFolder.parentFile.walkTopDown() + sequenceOf(baseTypescriptFolder),
        baseTypescriptFolder
    )
    val replacements = Replacements()
    baseTypescriptFolder.parentFile.walkTopDown()
        .filter { it.name.endsWith(".ts.yml") || it.name.endsWith(".ts.yaml") }
        .forEach {
            try {
                replacements += it
            } catch(e:Exception){
                throw IllegalArgumentException("Could not parse $it", e)
            }
        }
    val layouts = jacksonObjectMapper().readValue<Map<String, AndroidLayoutFile>>(androidLayoutsSummaryFile)
    for((name, layout) in layouts){
        val outputFile = outputFolder.resolve(name.camelCase().plus("Xml.ts"))
        outputFile.bufferedWriter().use {
            val s = SmartTabWriter(it)
            layout.toTypescript(
                projectName = projectName,
                packageName = packageName,
                file = outputFile,
                base = baseTypescriptFolder,
                manifest = declarationManifest,
                replacements = replacements,
                out = s
            )
        }
    }
}

fun AndroidLayoutFile.toTypescript(
    projectName: String?,
    packageName: String,
    file: File,
    base: File,
    manifest: DeclarationManifest,
    replacements: Replacements,
    out: Appendable
) {
    log("Creating ${name}Xml.ts...")
    fun String.toTsType(): String {
        return (replacements.types[this]?.firstOrNull()
            ?: replacements.types["android.widget.$this"]?.firstOrNull()
            ?: replacements.types["android.view.$this"]?.firstOrNull()
                )?.template?.parts?.joinToString("") { (it as? TemplatePart.Text)?.string ?: "" } ?: this.substringAfterLast('.')
    }
    fun String.getImport(): TemplatePart.Import? {
        return (replacements.types[this]?.firstOrNull()
            ?: replacements.types["android.widget.$this"]?.firstOrNull()
            ?: replacements.types["android.view.$this"]?.firstOrNull()
                )?.template?.find { it is TemplatePart.Import } as? TemplatePart.Import ?:
            manifest.importLine(file.relativeTo(base), this, this.substringAfterLast('.'))
    }

    val imports = listOf(
        bindings.mapNotNull {
            it.value.type.getImport()
        },
        delegateBindings.mapNotNull {
            it.value.type.getImport()
        },
        sublayouts.map { TemplatePart.Import("./" + it.value.layoutXmlClass, it.value.layoutXmlClass) }
    ).flatten()

    out.appendln("//")
    out.appendln("// ${name}Xml.ts")
    out.appendln("// Created by Khrysalis XML Typescript")
    out.appendln("//")
    out.appendln("import { loadHtmlFromString, findViewById, getViewById, replaceViewWithId } from 'butterfly/dist/views/html'")
    out.appendln("import { customViewSetDelegate } from 'butterfly/dist/views/CustomView'")
    renderImports(projectName, file.relativeTo(base).path, imports, out)
    for(variant in variants) {
        out.append("import htmlFor")
        out.append(variant.camelCase())
        out.append(" from '../layout-")
        out.append(variant)
        out.append("/")
        out.append(this.fileName)
        out.appendln(".html'")
    }
    out.appendln("import htmlForDefault from './$fileName.html'")
    out.appendln("//! Declares ${packageName}.layouts.${name}Xml")
    out.appendln("export class ${name}Xml {")
    out.appendln("xmlRoot!: HTMLElement;")
    bindings.values.forEach {
        if(it.optional){
            out.appendln(it.run { "${name.safeJsIdentifier()}: ${type.toTsType()} | null;" })
        } else {
            out.appendln(it.run { "${name.safeJsIdentifier()}!: ${type.toTsType()};" })
        }
    }
    delegateBindings.values.forEach {
        if(it.optional){
            out.appendln(it.run { "${name}Delegate: ${type.toTsType()} | null;" })
        } else {
            out.appendln(it.run { "${name}Delegate!: ${type.toTsType()};" })
        }
    }
    sublayouts.values.forEach {
        if(it.optional){
            out.appendln(it.run { "${name.safeJsIdentifier()}: ${layoutXmlClass} | null;" })
        } else {
            out.appendln(it.run { "${name.safeJsIdentifier()}!: ${layoutXmlClass};" })
        }
    }
    out.appendln("loadHtmlString(): string {")
    for(variant in variants.sortedDescending()) {
        when(variant.firstOrNull()){
            'w' -> {
                val w = variant.substring(1).filter { it.isDigit() }.toInt()
                out.appendln("if (window.innerWidth > $w) return htmlFor${variant.camelCase()}")
            }
        }
    }
    out.appendln("return htmlForDefault;")
    out.appendln("}")
    out.appendln("setup(dependency: Window): HTMLElement {")
    out.appendln("const view = loadHtmlFromString(this.loadHtmlString());")
    out.appendln("this.xmlRoot = view")
    bindings.values.forEach {
        if(it.optional){
            out.appendln(it.run { "this.${name.safeJsIdentifier()} = findViewById<${type.toTsType()}>(view, \"$resourceId\");" })
        } else {
            out.appendln(it.run { "this.${name.safeJsIdentifier()} = getViewById<${type.toTsType()}>(view, \"$resourceId\");" })
        }
    }
    delegateBindings.values.forEach {
        out.appendln("if(this.${it.name.safeJsIdentifier()}){ this.${it.name}Delegate = new ${it.type.toTsType()}(); customViewSetDelegate(this.${it.name.safeJsIdentifier()}, this.${it.name}Delegate); }")
    }
    sublayouts.values.forEach {
        out.appendln(it.run { "replaceViewWithId(view, ()=>{ " })
        out.appendln(it.run { "this.${name.safeJsIdentifier()} = new $layoutXmlClass();" })
        out.appendln(it.run { "return this.${name.safeJsIdentifier()}.setup(dependency);" })
        out.appendln(it.run { "}, \"$resourceId\");" })
    }
    out.appendln("return view")
    out.appendln("}")
    out.appendln("}")
}
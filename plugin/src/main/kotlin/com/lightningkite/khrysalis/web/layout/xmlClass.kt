package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.typescript.DeclarationManifest
import com.lightningkite.khrysalis.typescript.renderImports
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File


fun AndroidLayoutFile.toTypescript(
    projectName: String?,
    packageName: File,
    file: File,
    base: File,
    manifest: DeclarationManifest,
    out: Appendable
) {
    val imports = listOf(
        bindings.mapNotNull {
            manifest.importLine(file.relativeTo(base), it.value.type, it.value.type.substringAfter('.'))
        },
        delegateBindings.mapNotNull {
            manifest.importLine(file.relativeTo(base), it.value.type, it.value.type.substringAfter('.'))
        },
        sublayouts.map { TemplatePart.Import("./" + it.value.layoutXmlClass, it.value.layoutXmlClass) }
    ).flatten()

    out.appendln("//")
    out.appendln("// ${name}Xml.ts")
    out.appendln("// Created by Khrysalis XML Typescript")
    out.appendln("//")
    out.appendln("import { loadHtmlFromString, findViewById, getViewById, replaceViewWithId } from 'khrysalis/dist/views/html'")
    renderImports(projectName, imports, out)
    for(variant in variants) {
        out.append("import htmlFor")
        out.append(variant.camelCase())
        out.append(" from 'layouts-")
        out.append(variant)
        out.append("/")
        out.append(this.fileName)
        out.appendln(".html'")
    }
    out.appendln("import htmlForDefault from 'layouts/$fileName.html'")
    out.appendln("//! Declares ${packageName}.${name}Xml")
    out.appendln("export class ${name}Xml {")
    out.appendln("xmlRoot!: HTMLElement;")
    bindings.values.forEach {
        if(it.optional){
            out.appendln(it.run { "$name?: $type;" })
        } else {
            out.appendln(it.run { "$name!: $type;" })
        }
    }
    delegateBindings.values.forEach {
        if(it.optional){
            out.appendln(it.run { "$name?: $type;" })
        } else {
            out.appendln(it.run { "$name!: $type;" })
        }
    }
    sublayouts.values.forEach {
        if(it.optional){
            out.appendln(it.run { "$name?: $layoutXmlClass;" })
        } else {
            out.appendln(it.run { "$name!: $layoutXmlClass;" })
        }
    }
    out.appendln("loadHtmlString(): string {")
    for(variant in variants.sortedDescending()) {
        when(variant.firstOrNull()){
            'w' -> {
                val w = variant.substring(1).toInt()
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
            out.appendln(it.run { "this.$name = findViewById<$type>(view, \"$resourceId\");" })
        } else {
            out.appendln(it.run { "this.$name = getiewById<$type>(view, \"$resourceId\");" })
        }
    }
    delegateBindings.values.forEach {
        out.appendln("const ${it.name}View = this.${it.resourceId.camelCase()}")
        out.appendln("if(${it.name}View){ this.${it.name} = new ${it.type}(); ${it.name}View.delegate = this.${it.name}; }")
    }
    sublayouts.values.forEach {
        out.appendln(it.run { "replaceViewWithId<HTMLDivElement>(view, ()=>{ " })
        out.appendln(it.run { "this.$name = new $layoutXmlClass();" })
        out.appendln(it.run { "return this.$name.setup(dependency);" })
        out.appendln(it.run { "}, \"$resourceId\");" })
    }
    out.appendln("return view")
    out.appendln("}")
    out.appendln("}")
}
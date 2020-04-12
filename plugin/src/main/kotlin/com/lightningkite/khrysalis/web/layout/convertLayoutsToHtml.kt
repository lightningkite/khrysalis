package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.ios.layout.*
import com.lightningkite.khrysalis.ios.values.readXMLStrings
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import com.lightningkite.khrysalis.web.layout.drawables.convertDrawablesToWeb
import com.lightningkite.khrysalis.web.layout.values.translateXmlColorSetToCss
import com.lightningkite.khrysalis.web.layout.values.translateXmlColorsToCss
import com.lightningkite.khrysalis.web.layout.values.translateXmlDimensionsToCss
import java.io.File
import java.lang.StringBuilder

fun convertLayoutsToHtml(
    androidMainFolder: File,
    webFolder: File,
    converter: HtmlTranslator = HtmlTranslator()
) {
    converter.styles = androidMainFolder.resolve("res/values/styles.xml").readXMLStyles()
    converter.strings = androidMainFolder.resolve("res/values/strings.xml").readXMLStrings()
    converter.outFolder = webFolder

    val scssFile = webFolder.resolve("src/main.css").also { it.parentFile.mkdirs() }
    scssFile.printWriter().use { out ->
        out.appendln("/*Generated by Khrysalis*/")
        out.appendln("""
html,
body {
    height: 100%;
    width: 100%;
    margin: 0;
}
body > * {
    height: 100%;
    width: 100%;
}
* {
    overflow-x: hidden;
    overflow-y: hidden;
    box-sizing: border-box;
    font-family: "Roboto", "Open Sans", sans-serif;
}
button {
    border: none;
    background: none;
    text-transform: uppercase;
    transition-duration: 0.2s;
}
button:hover {
    box-shadow: 0 2px 2px 0 rgba(0,0,0,0.24), 0 4px 8px 0 rgba(0,0,0,0.19);
}
      """)
        androidMainFolder.resolve("res/values/colors.xml").takeIf { it.exists() }?.let {
            translateXmlColorsToCss(it, out)
        }
        androidMainFolder.resolve("res/color").walkTopDown().filter { it.extension == "xml" }.forEach {
            translateXmlColorSetToCss(it, out)
        }
        androidMainFolder.resolve("res/values/dimens.xml").takeIf { it.exists() }?.let {
            translateXmlDimensionsToCss(it, out)
        }
        convertDrawablesToWeb(androidMainFolder.resolve("res"), webFolder, out)
    }

    val styles = androidMainFolder.resolve("res/values/styles.xml").takeIf { it.exists() }?.readXMLStyles() ?: mapOf()
    webFolder.resolve("layouts").apply {
        deleteRecursively()
        mkdirs()
    }

    androidMainFolder.resolve("res/layout").walkTopDown()
        .filter { it.extension == "xml" }
        .forEach { item ->
            log(item.toString())

            val appendable = StringBuilder()
            converter.styles = styles
            val root = XmlNode.read(item, styles)
            converter.emitFile(root, appendable)
            val output = appendable.toString()

            webFolder.resolve("src/layouts").resolve(item.nameWithoutExtension + ".html").also{
                it.parentFile.mkdirs()
            }.writeTextIfDifferent(output)
        }
}

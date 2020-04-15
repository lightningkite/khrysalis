package com.lightningkite.khrysalis.web.layout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lightningkite.khrysalis.android.layout.readLayoutInfo
import com.lightningkite.khrysalis.ios.layout.*
import com.lightningkite.khrysalis.ios.values.readXMLStrings
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.writeTextIfDifferent
import com.lightningkite.khrysalis.web.layout.drawables.convertDrawablesToWeb
import com.lightningkite.khrysalis.web.layout.values.getXmlStrings
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
    converter.strings = getXmlStrings(androidMainFolder.resolve("res/values/strings.xml"))
    converter.outFolder = webFolder

    val scssFile = webFolder.resolve("src/main.css").also { it.parentFile.mkdirs() }
    scssFile.printWriter().use { out ->
        out.appendln("/*Generated by Khrysalis*/")
        out.appendln(
            """
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
    flex-shrink: 0;
    font-family: "Roboto", "Open Sans", sans-serif;
    max-width: 100%;
    max-height: 100%;
}
.khrysalis-scroll-view > * {
    max-height: none;
}
button {
    border: none;
    background: none;
    text-transform: uppercase;
    transition-duration: 0.2s;
}
input {
    background: none;
    border: none;
    border-bottom: 1px solid var(--color-foreground-fade, gray);
}
input:focus {
    border-bottom: 2px solid var(--color-color-primary, blue);
}
button:hover {
    box-shadow: 0 2px 2px 0 rgba(0,0,0,0.24), 0 4px 8px 0 rgba(0,0,0,0.19);
}

.khrysalis-switch {
    display: flex;
    flex-direction: row;
}
.khrysalis-switch > div {
    flex-grow: 1;
}
.khrysalis-switch > input {
    display: none;
}
.khrysalis-switch-back {
    position: relative;
    width: 40px;
    height: 24px;
    border-radius: 12px;
    background-color: var(--color-background-fade, gray);
}
.khrysalis-switch-front {
    position: absolute;
    width: 20px;
    height: 20px;
    right: 18px;
    top: 50%;
    transform: translateY(-50%);
    border-radius: 50%;
    background-color: var(--color-color-accent, blue);
    -webkit-transition: 0.2s;
    transition: 0.2s;
}
:checked ~ .khrysalis-switch-back > .khrysalis-switch-front {
    right: 2px;
}
      """
        )
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
    webFolder.resolve("src").listFiles()!!.filter { it.name.contains("layout") }.forEach {
        it.deleteRecursively()
        it.mkdirs()
    }

    val layoutInfo = readLayoutInfo(androidMainFolder.resolve("../../build"))
    val manifestFile = webFolder.resolve("src/layout/manifest.json")
        .also { it.parentFile.mkdirs() }
    manifestFile.writeText(
        jacksonObjectMapper().writeValueAsString(
            layoutInfo.values.mapNotNull {
                it.fileName to (it.variants.takeUnless { it.isEmpty() } ?: return@mapNotNull null)
            }.associate { it }
        ).also { println(it) }
    )
    androidMainFolder.resolve("res").listFiles()!!
        .asSequence()
        .filter { it.name.contains("layout") }
        .forEach { folder ->

            folder.walkTopDown()
                .filter { it.extension == "xml" }
                .forEach { item ->
                    log(item.toString())

                    val appendable = StringBuilder()
                    converter.styles = styles
                    val root = XmlNode.read(item, styles, androidMainFolder.resolve("res/layout"))
                    converter.emitFile(root, appendable)
                    val output = appendable.toString()

                    webFolder.resolve("src").resolve(folder.name).resolve(item.nameWithoutExtension + ".html").also {
                        it.parentFile.mkdirs()
                    }.writeTextIfDifferent(output)
                }
        }

}

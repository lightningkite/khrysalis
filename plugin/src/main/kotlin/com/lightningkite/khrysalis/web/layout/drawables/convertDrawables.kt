package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import com.lightningkite.khrysalis.web.layout.WebResources
import java.io.File
import java.lang.Appendable

fun convertDrawablesToWeb(
    androidResourcesFolder: File,
    webFolder: File,
    out: Appendable,
    resources: WebResources
) {
    val webFolderImages = webFolder.resolve("src/images").also {
        it.listFiles()?.forEach { it.delete() }
        it.mkdirs()
    }
    convertPngs(out, androidResourcesFolder, webFolderImages, resources)
    convertDrawableXmls(out, androidResourcesFolder, webFolderImages, resources)
}

package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.io.File
import java.lang.Appendable

fun convertDrawablesToWeb(
    androidResourcesFolder: File,
    webFolder: File,
    out: Appendable
) {
    val webFolderImages = webFolder.resolve("src/images").also {
        it.listFiles()?.forEach { it.delete() }
        it.mkdirs()
    }
    convertPngs(out, androidResourcesFolder, webFolderImages)
    convertDrawableXmls(out, androidResourcesFolder, webFolderImages)
}

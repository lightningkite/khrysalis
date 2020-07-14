package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*
import java.io.File
import java.lang.Exception

fun convertDrawablesToIos(
    androidResourcesFolder: File,
    iosResourcesSwiftFolder: File,
    iosAssetsFolder: File
) {
    iosResourcesSwiftFolder.resolve("drawable").let {
        it.listFiles()?.forEach { it.delete() }
        it.mkdirs()
    }
    convertPngs(androidResourcesFolder, iosAssetsFolder, iosResourcesSwiftFolder)
    convertDrawableXmls(androidResourcesFolder, iosResourcesSwiftFolder)
}

package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.*
import java.io.File
import java.lang.Exception

fun convertDrawablesToIos(
    androidResourcesFolder: File,
    iosResourcesSwiftFolder: File,
    iosAssetsFolder: File
) {
    convertPngs(androidResourcesFolder, iosAssetsFolder)
    convertDrawableXmls(androidResourcesFolder, iosResourcesSwiftFolder)
}

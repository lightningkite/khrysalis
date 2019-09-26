package com.lightningkite.kwift

import com.lightningkite.kwift.drawables.convertDrawablesToIos
import com.lightningkite.kwift.values.convertResourceValuesToIos
import java.io.File

fun convertResourcesToIos(
    androidResourcesFolder: File,
    baseFolderForLocalizations: File,
    iosAssetsFolder: File,
    iosResourcesSwiftFolder: File
) {
    convertResourceValuesToIos(
        androidResourcesFolder = androidResourcesFolder,
        baseFolderForLocalizations = baseFolderForLocalizations,
        iosResourcesSwiftFolder = iosResourcesSwiftFolder
    )
    convertDrawablesToIos(
        androidResourcesFolder = androidResourcesFolder,
        iosAssetsFolder = iosAssetsFolder,
        iosResourcesSwiftFolder = iosResourcesSwiftFolder
    )
}

fun convertResourcesToIos(
    androidFolder: File,
    iosFolder: File
) = convertResourcesToIos(
    androidResourcesFolder = androidFolder.resolve("src/main/res"),
    baseFolderForLocalizations = iosFolder.resolve("localizations"),
    iosAssetsFolder = iosFolder.resolve("Assets.xcassets"),
    iosResourcesSwiftFolder = iosFolder.resolve("swiftResources")
)

package com.lightningkite.kwift.drawables

import com.lightningkite.kwift.utils.*
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
    val main = iosResourcesSwiftFolder.resolve("drawable").also { it.mkdirs() }.resolve("ResourcesDrawables.swift")
    println("Writing $main")
    main.writeText("""
        //Automatically created by Kwift
        //Extended by other files.
        import UIKit
        enum ResourcesDrawables {}
    """.trimIndent())
    convertPngs(androidResourcesFolder, iosAssetsFolder, iosResourcesSwiftFolder)
    convertDrawableXmls(androidResourcesFolder, iosResourcesSwiftFolder)
}

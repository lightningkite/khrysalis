package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.*
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
    main.writeTextIfDifferent("""
        //Automatically created by Khrysalis
        //Extended by other files.
        import UIKit
        enum ResourcesDrawables {}
    """.trimIndent())
    convertPngs(androidResourcesFolder, iosAssetsFolder, iosResourcesSwiftFolder)
    convertDrawableXmls(androidResourcesFolder, iosResourcesSwiftFolder)
}

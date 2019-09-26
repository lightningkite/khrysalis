package com.lightningkite.kwift.drawables

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File


fun convertPngs(
    resourcesFolder: File,
    assetsFolder: File
) {
    val pngNames = (resourcesFolder.listFiles() ?: arrayOf())
        .asSequence()
        .filter { it.name.startsWith("drawable") }
        .flatMap { it.walkTopDown() }
        .filter { it.extension == "png" }
        .map { it.nameWithoutExtension }
        .distinct()
        .toSet()

    pngNames.forEach { pngName ->
        val matching = (resourcesFolder.listFiles() ?: arrayOf())
            .asSequence()
            .filter { it.name.startsWith("drawable") }
            .flatMap { it.walkTopDown() }
            .filter { it.name == pngName + ".png" }

        val one = matching.find { it.parent.contains("drawable-ldpi") || it.parent.contains("drawable-mdpi") }
        val two = matching.find { it.parent.contains("drawable-hdpi") }
        val three = matching.find { it.parent.contains("drawable-xhdpi") }

        if(one == null && two == null && three == null) return@forEach

        val iosFolder = assetsFolder.resolve(pngName + ".imageset").apply { mkdirs() }
        jacksonObjectMapper().writeValue(
            iosFolder.resolve("Contents.json"),
            mapOf(
                "info" to mapOf("version" to 1, "author" to "xcode"),
                "images" to listOf(one, two, three).mapIndexed { index, file ->
                    if(file == null) return@mapIndexed null
                    mapOf("idiom" to "universal", "filename" to file.name, "scale" to "${index + 1}x")
                }.filterNotNull()
            )
        )
        listOf(one, two, three).filterNotNull().forEach {
            it.copyTo(iosFolder.resolve(it.name), overwrite = true)
        }
    }
}

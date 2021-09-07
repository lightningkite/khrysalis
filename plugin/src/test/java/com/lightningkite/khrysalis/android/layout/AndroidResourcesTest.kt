package com.lightningkite.khrysalis.android.layout

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class AndroidResourcesTest {
    @Test
    fun testTemplateProject() {
        val projectBase = File("../../../khrysalis-template/android/src/main/res")
        println("Here: " + File(".").absolutePath)
        assertTrue(projectBase.exists())
        val resources = AndroidResources()
        resources.parse(projectBase)
        val all = resources.styles.mapKeys { "styles-${it.key}" } +
            resources.colors.mapKeys { "colors-${it.key}" } +
            resources.drawables.mapKeys { "drawables-${it.key}" } +
            resources.fonts.mapKeys { "fonts-${it.key}" } +
            resources.strings.mapKeys { "strings-${it.key}" } +
            resources.dimensions.mapKeys { "dimensions-${it.key}" } +
            resources.layouts.mapKeys { "layouts-${it.key}" }
        all.entries.sortedBy { it.key }.forEach { println("${it.key}: ${it.value}") }
    }
}
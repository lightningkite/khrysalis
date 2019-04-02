package com.lightningkite.kwift.gradle

import org.gradle.api.Project
import java.io.File

open class KwiftXmlPluginExtension(project: Project) {
    var resourcesDirectory: File? = null
    var outputDirectory: File? = null

    override fun toString(): String {
        return "KwiftXmlPluginExtension($resourcesDirectory, $outputDirectory)"
    }
}

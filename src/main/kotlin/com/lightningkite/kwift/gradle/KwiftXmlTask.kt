package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.layoutxml.xmlTask
import com.lightningkite.kwift.swift.kwiftTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KwiftXmlTask : DefaultTask() {
    var resourcesDirectory: File? = null
    var outputDirectory: File? = null

    init {
        group = "build"
    }

    @TaskAction
    fun writeXmlResources() {
        val resourcesDirectory = resourcesDirectory ?: project.extensions.findByName("kwiftXml")
            ?.let { it as KwiftXmlPluginExtension }?.resourcesDirectory ?: return
        val outputDirectory = outputDirectory ?: project.extensions.findByName("kwiftXml")
            ?.let { it as KwiftXmlPluginExtension }?.outputDirectory ?: return
        xmlTask(resourcesFolder = resourcesDirectory, outputFolder = outputDirectory)
    }

}

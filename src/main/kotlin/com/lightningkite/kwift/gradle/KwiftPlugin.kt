package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.javaify
import com.lightningkite.kwift.javaifyWithDots
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import java.io.File


open class KwiftPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("kwift", KwiftPluginExtension::class.java, target)

        val task = target.tasks.create("kwift", KwiftTask::class.java)
    }
}

open class KwiftPluginExtension(project: Project) {

    @InputDirectory
    var inputDirectory: File = project.file("src")

    @OutputDirectory
    var outputDirectory: File = project.file("build/swift")

    override fun toString(): String {
        return "KwiftPluginExtension($inputDirectory, $outputDirectory)"
    }
}

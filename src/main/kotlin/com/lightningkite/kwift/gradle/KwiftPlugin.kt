package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.javaify
import com.lightningkite.kwift.javaifyWithDots
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


open class KwiftPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("kwift", KwiftPluginExtension::class.java)
        val task = target.tasks.create("mirror", KwiftTask::class.java)
        task.inputDirectory = extension.inputDirectory
        task.outputDirectory = extension.outputDirectory
    }
}

open class KwiftPluginExtension {
    var inputDirectory: File = File("src")
    var outputDirectory: File = File("build/swift")
}

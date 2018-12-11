package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.kwiftTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KwiftTask : DefaultTask() {
    var inputDirectory: File? = null
    var outputDirectory: File? = null

    init {
        group = "build"
    }

    @TaskAction
    fun writeReflectiveFiles() {

        val inputDirectory: File = inputDirectory ?: project.extensions.findByName("kwift")?.let{ it as? KwiftPluginExtension }?.inputDirectory ?: File("src")
        val outputDirectory: File = outputDirectory ?: project.extensions.findByName("kwift")?.let{ it as? KwiftPluginExtension }?.outputDirectory ?: File("build/swift")

        println("Kwift - input: $inputDirectory, output: $outputDirectory")
        kwiftTask(
            directory = inputDirectory,
            outputDirectory = outputDirectory
        )
    }

}

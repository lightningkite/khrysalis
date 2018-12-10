package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.kwiftTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KwiftTask() : DefaultTask() {
    var inputDirectory: File = File("src")
    var outputDirectory: File = File("build/swift")

    init {
        group = "build"
    }

    @TaskAction
    fun writeReflectiveFiles() {
        kwiftTask(
            directory = inputDirectory,
            outputDirectory = outputDirectory
        )
    }

}

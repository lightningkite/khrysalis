package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.kwiftTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class KwiftTask() : DefaultTask() {

    init {
        group = "build"
    }

    @TaskAction
    fun writeReflectiveFiles() {
        kwiftTask(
            directory = project.file("src"),
            outputDirectory = project.file("build/swift")
        )
    }

}

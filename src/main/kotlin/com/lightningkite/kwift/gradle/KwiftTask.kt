package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.swift.kwiftTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KwiftTask : DefaultTask() {
    var directoryPairs: List<Pair<File, File>>? = null

    init {
        group = "build"
    }

    @TaskAction
    fun writeReflectiveFiles() {

        val pairs = directoryPairs ?: project.extensions.findByName("kwift")
            ?.let{ it as? KwiftPluginExtension }
            ?.directoryPairs
            ?.map { project.file(it[0]) to project.file(it[1]) }

        if(pairs != null){
            println("Kwift - pairs: $pairs")
            kwiftTask(pairs)
        } else {
            println("Kwift - No pairs of directories found, skipping")
        }
    }

}

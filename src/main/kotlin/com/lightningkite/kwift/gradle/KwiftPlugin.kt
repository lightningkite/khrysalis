package com.lightningkite.kwift.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


open class KwiftPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("kwift", KwiftPluginExtension::class.java, target)
        val task = target.tasks.create("kwift", KwiftTask::class.java)
    }
}

open class KwiftPluginExtension(project: Project) {

    var directoryPairs: List<List<String>> = listOf(listOf("src", "build/swift"))

    override fun toString(): String {
        return "KwiftPluginExtension($directoryPairs)"
    }
}

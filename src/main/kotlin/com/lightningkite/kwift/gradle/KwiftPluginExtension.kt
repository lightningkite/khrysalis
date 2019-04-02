package com.lightningkite.kwift.gradle

import org.gradle.api.Project

open class KwiftPluginExtension(project: Project) {

    var directoryPairs: List<List<String>> = listOf(listOf("src", "build/swift"))

    override fun toString(): String {
        return "KwiftPluginExtension($directoryPairs)"
    }
}

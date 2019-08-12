package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.swift.SwiftListener
import org.gradle.api.Project

open class KwiftPluginExtension(project: Project) {

    var directoryPairs: List<List<String>> = listOf(listOf("src", "build/swift"))
    var swiftListenerSetup: SwiftListener.()->Unit = {}

    override fun toString(): String {
        return "KwiftPluginExtension($directoryPairs)"
    }
}

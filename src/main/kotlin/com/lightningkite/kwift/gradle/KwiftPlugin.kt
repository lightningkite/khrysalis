package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.javaify
import com.lightningkite.kwift.javaifyWithDots
import org.gradle.api.Plugin
import org.gradle.api.Project


open class KwiftPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val task = target.tasks.create("mirror", KwiftTask::class.java)
    }
}


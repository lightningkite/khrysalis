package com.lightningkite.kwift.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


open class KwiftPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        run {
            val extension = target.extensions.create("kwift", KwiftPluginExtension::class.java, target)
            val task = target.tasks.create("kwift", KwiftTask::class.java)
        }
        run {
            val extension = target.extensions.create("kwiftXml", KwiftXmlPluginExtension::class.java, target)
            val task = target.tasks.create("kwiftXml", KwiftXmlTask::class.java)
        }
    }
}



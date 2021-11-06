package com.lightningkite.khrysalis.kotlin

import com.lightningkite.khrysalis.generic.CompilerPluginUseInfo
import org.gradle.api.Project

fun kotlinPluginUse(project: Project): CompilerPluginUseInfo = CompilerPluginUseInfo(
    cacheName = "kotlin.jar",
    project = project,
    options = listOf()
)
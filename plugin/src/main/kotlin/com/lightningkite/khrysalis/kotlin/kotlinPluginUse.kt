package com.lightningkite.khrysalis.kotlin

import com.lightningkite.khrysalis.generic.CompilerPluginUseInfo
import org.gradle.api.Project

fun kotlinPluginUse(project: Project): CompilerPluginUseInfo = CompilerPluginUseInfo(
    project = project,
    configName = "khrysalisKotlin",
    options = listOf()
)
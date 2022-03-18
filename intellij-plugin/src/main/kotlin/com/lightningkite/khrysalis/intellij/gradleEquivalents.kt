package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.gradle.util.GradleConstants
import java.io.File
import com.intellij.openapi.module.Module
import java.util.*

fun Project.khrysalisEquivalents(module: Module): List<File> {
    val projectsData = ProjectDataManager.getInstance().getExternalProjectsData(this, GradleConstants.SYSTEM_ID)
    for (projectInfo in projectsData) {
        val projectDataNode = projectInfo.externalProjectStructure ?: continue
        try {
            for (moduleNode in ExternalSystemApiUtil.findAll(projectDataNode, ProjectKeys.MODULE)) {
                Logger.getInstance("com.lightningkite.khrysalis.intellij.gradleEquivalents.kt").warn(
                    """
                         module.name: ${module.name}
                         moduleNode.data.moduleName: ${moduleNode.data.moduleName}
                         moduleNode.data.group: ${moduleNode.data.group}
                         moduleNode.data.ideGrouping: ${moduleNode.data.ideGrouping}
                         moduleNode.data.ideModuleGroup: ${moduleNode.data.ideModuleGroup}
                         moduleNode.data.ideParentGrouping: ${moduleNode.data.ideParentGrouping}
                         comparing: ${module.name.substringBeforeLast('.')} == ${moduleNode.data.ideGrouping}
                    """.trimIndent()
                )
                if(module.name.substringBeforeLast('.') == moduleNode.data.ideGrouping) {
                    return ExternalSystemApiUtil.find(
                        moduleNode!!, KhrysalisProjectResolveExtension.KEY
                    )?.data?.also { Logger.getInstance("com.lightningkite.khrysalis.intellij.gradleEquivalents.kt").warn("Found ${it.files}") }?.files ?: listOf()
                }
            }
        } catch (e: ClassCastException) {
            // catch deserialization issue caused by fast serializer
            Logger.getInstance("com.lightningkite.khrysalis.intellij.gradleEquivalents.kt").debug(e)
        }
    }
    return listOf()
}
package com.lightningkite.khrysalis.web

import com.google.auto.service.AutoService
import com.lightningkite.khrysalis.gradle.KhrysalisPluginExtension
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.konan.file.File

@AutoService(KotlinGradleSubplugin::class)
class TypescriptSubPlugin: KotlinGradleSubplugin<AbstractCompile> {

    companion object {
        var enabled = false
    }

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val ext = project.extensions.findByType(KhrysalisPluginExtension::class.java) ?: return emptyList()
        val allEq = ext.equivalentsDirectories + project.projectDir.resolve("src/main/equivalents")
        println("Applying compiler plugin...")
        return listOf(
            SubpluginOption("equivalents", allEq.joinToString(File.pathSeparator) { it.path }),
            SubpluginOption("outputDirectory", project.projectDir.resolve("../web/src").path)
        )
    }

    override fun getCompilerPluginId(): String = "com.lightningkite.khrysalis.typescript"
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact("com.lightningkite.khrysalis", "kotlin-compiler-plugin-typescript", "0.1.0")
    override fun isApplicable(project: Project, task: AbstractCompile): Boolean = enabled
}
package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.generic.CompilerRunInfo
import com.lightningkite.khrysalis.generic.runCompiler
import com.lightningkite.khrysalis.ios.swift.*
import com.lightningkite.khrysalis.kotlin.kotlinPluginUse
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.typescriptPluginUse
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.*
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.ir.backend.js.compile
import java.io.File
import java.util.*

open class KhrysalisPluginExtension {
    open var organizationName: String = "Organization"
    open var projectName: String? = null
    open var iosSourceFolder: File? = null
    open var iosProjectFolder: File? = null
    open var webSourceFolder: File? = null
    open var webProjectFolder: File? = null
    open var libraryMode: Boolean = false

    open var overrideIosFolder: File?
        get() = iosProjectFolder
        set(value) { iosProjectFolder = value }
    open var overrideWebFolder: File?
        get() = webProjectFolder
        set(value) { webProjectFolder = value }

    override fun toString(): String {
        return "(" +
                "\norganizationName: " + organizationName +
                "\nprojectName: " + projectName +
                "\n)"
    }

    private var completed: KhrysalisExtensionSettings? = null
    fun complete(project: Project): KhrysalisExtensionSettings = completed ?: run {
        val libraryMode = this.libraryMode
        val organizationName = this.organizationName
        val projectName = this.projectName ?: project.name.takeUnless { it == "app" || it == "android" } ?: project.rootProject.name
        val iosProjectFolder = this.iosProjectFolder ?: project.projectDir.resolve("../ios")
        val iosSourceFolder = this.iosSourceFolder ?: (if(libraryMode) iosProjectFolder.resolve(projectName).resolve("Classes") else iosProjectFolder.resolve(projectName).resolve("src"))
        val webProjectFolder = this.webProjectFolder ?: project.projectDir.resolve("../web")
        val webSourceFolder = this.webSourceFolder ?: webProjectFolder.resolve("src")
        val result = KhrysalisExtensionSettings(
            organizationName = organizationName,
            projectName = projectName,
            iosProjectFolder = iosProjectFolder,
            iosSourceFolder = iosSourceFolder,
            webProjectFolder = webProjectFolder,
            webSourceFolder = webSourceFolder,
            libraryMode = libraryMode,
        )
        this.completed = result
        result
    }
}

data class KhrysalisExtensionSettings(
    val organizationName: String,
    val projectName: String,
    val iosProjectFolder: File,
    val iosSourceFolder: File,
    val webProjectFolder: File,
    val webSourceFolder: File,
    val libraryMode: Boolean,
)

fun Project.khrysalis(configure: Action<KhrysalisPluginExtension>) {
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("khrysalis", configure)
}
fun DependencyHandler.khrysalis(dependencyNotation: Any): Dependency? = add("kcp", dependencyNotation)
fun DependencyHandler.khrysalisSwift(dependencyNotation: Any): Dependency? = add("kcp", dependencyNotation)
fun DependencyHandler.khrysalisTypescript(dependencyNotation: Any): Dependency? = add("kcp", dependencyNotation)
fun DependencyHandler.khrysalisKotlin(dependencyNotation: Any): Dependency? = add("kcp", dependencyNotation)

val Project.khrysalis: KhrysalisExtensionSettings get() = (project.extensions.getByName("khrysalis") as KhrysalisPluginExtension).complete(this)

abstract class TranspileTask(): SourceTask() {
    @get:Input val projectName: String by lazy { project.khrysalis.projectName }
    @get:Input val libraryMode: Boolean by lazy { project.khrysalis.libraryMode }
    @get:OutputDirectory var outputDirectory: File = File("")
}

fun KotlinCompile.calculateCommonPackage(): String {
    return source
        .asSequence()
        .filter { it.name.endsWith(".kt") }
        .map { it.readText().substringAfter("package ").substringBefore('\n').trim() }
        .reduce { acc, s -> acc.commonPrefixWith(s) }
}

class KhrysalisPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configurations.maybeCreate("kcp")
        val isMac = Os.isFamily(Os.FAMILY_MAC)
        val project = target
        val extension = project.extensions.create<KhrysalisPluginExtension>("khrysalis", KhrysalisPluginExtension::class.java)

        project.configurations.maybeCreate("kcp").apply {
            description = "Kotlin compiler plugin dependencies"
            isCanBeResolved = true
            isCanBeConsumed = false
            isVisible = true
        }

        KhrysalisSettings.verbose = true
        val projectName by lazy { target.khrysalis.projectName }
        val webBase by lazy { target.khrysalis.webProjectFolder }
        val webSrc by lazy { target.khrysalis.webSourceFolder }
        val iosBase by lazy { target.khrysalis.iosProjectFolder }
        val iosSrc by lazy { target.khrysalis.iosSourceFolder }

        //IOS

        project.afterEvaluate {
            it.tasks.filterIsInstance<KotlinCompile>().forEach { c ->

                it.tasks.create("${c.name}ToSwift", TranspileTask::class.java) {
                    it.group = "ios"
                    it.dependsOn(project.configurations.getByName("kcp"))
                    it.outputDirectory = iosSrc
                    it.source(*project.swiftDependencies(iosBase).toList().toTypedArray())
                    it.doFirst {
                        c.incremental = false
                        c.plugin("swift") {
                            "outputDirectory" set iosSrc.absolutePath
                            "projName" set projectName
                            "equivalents" set project.swiftDependencies(iosBase).toList()
                            "commonPackage" set c.calculateCommonPackage()
                            "libraryMode" set extension.libraryMode.toString()
                        }
                    }
                    it.finalizedBy(c)
                }
                it.tasks.create("${c.name}ToTypescript", TranspileTask::class.java) {
                    it.group = "web"
                    it.dependsOn(project.configurations.getByName("kcp"))
                    it.outputDirectory = webSrc
                    it.source(webBase.resolve("node_modules"))
                    it.doFirst {
                        c.incremental = false
                        c.plugin("typescript") {
                            "outputDirectory" set webSrc.absolutePath
                            "projName" set projectName
                            "equivalents" set webBase.toString()
                            "commonPackage" set c.calculateCommonPackage()
                            "libraryMode" set extension.libraryMode.toString()
                        }
                    }
                    it.finalizedBy(c)
                }
            }
        }

        project.tasks.create("updateIosVersion") { task ->
            task.group = "ios"
            task.doLast {
                val versionName = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionName") as? String ?: project.version.toString()
                val versionCode = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionCode") as? Int ?: 0
                val projectFile = (iosBase.listFiles()?.toList()
                    ?.find { it.name.endsWith("xcodeproj", true) }
                    ?: throw IllegalStateException("Could not find projectFile at ${iosBase}"))
                    .resolve("project.pbxproj")
                    .also {
                        if(!it.exists()) {
                            throw IllegalStateException("Could not find projectFile at ${it}")
                        }
                    }
                projectFile.readText()
                    .replace(Regex("CURRENT_PROJECT_VERSION = [0-9]+;"), "CURRENT_PROJECT_VERSION = $versionCode;")
                    .replace(Regex("MARKETING_VERSION = [0-9.]+;"), "MARKETING_VERSION = $versionName;")
                    .let { projectFile.writeText(it) }
            }
        }


        //Web
        project.tasks.create("updateWebVersion") { task ->
            task.group = "web"
            task.doLast {
                val versionName = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionName") as? String ?: project.version.toString()
                val versionCode = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionCode") as? Int ?: 0
                val projectFile = webBase.resolve("package.json")
                projectFile.readText()
                    .replace(Regex(""""version": "([0-9.]+)""""), """"version": "$versionName"""")
                    .let { projectFile.writeText(it) }
                webBase.resolve("src/BuildConfig.ts").writeText("""
                    //! Declares com.tresitgroup.android.tresit.BuildConfig
                    export class BuildConfig {
                        static INSTANCE = BuildConfig
                        static VERSION_NAME: string = "$versionName"
                        static VERSION_CODE: number = $versionCode
                        static get DEBUG(): boolean {
                            return (window as any).isDebugMode ?? false
                        }
                    }
                """.trimIndent())
            }
        }
    }
}

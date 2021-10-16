package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.ios.swift.*
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.convertToTypescript
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Task
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.ir.backend.js.compile
import java.io.File
import java.util.*

open class KhrysalisPluginExtension {
    open var organizationName: String = "Organization"
    open var layoutPackage: String? = null
    open var projectName: String? = null
    open var overrideIosPackageName: String? = null
    open var overrideWebPackageName: String? = null
    open var overrideIosFolder: String? = null
    open var overrideWebFolder: String? = null

    override fun toString(): String {
        return "(" +
                "\norganizationName: " + organizationName +
                "\nprojectName: " + projectName +
                "\n)"
    }
}

class KhrysalisPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val isMac = Os.isFamily(Os.FAMILY_MAC)
        val project = target
        val ext = project.extensions.create<KhrysalisPluginExtension>("khrysalis", KhrysalisPluginExtension::class.java)

        fun extension() = ext
        fun projectName() = extension().projectName ?: project.name.takeUnless { it == "app" || it == "android" }
        ?: project.rootProject.name
        KhrysalisSettings.verbose = true
        fun androidBase() = project.projectDir
        fun webBase() = project.projectDir.resolve(ext.overrideWebFolder ?: "../web")
        fun iosBase() = project.projectDir.resolve(ext.overrideIosFolder ?: "../ios")
        fun iosFolder() = iosBase().resolve(projectName())
        fun packageName() =
            ext.layoutPackage ?: project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                ?.getProperty("applicationId") as? String ?: "unknown.packagename"

        fun androidSdkDirectory() =
            project.extensions.findByName("android")?.groovyObject?.getProperty("sdkDirectory") as? File

        fun sdkLevel() =
            (project.extensions.findByName("android")?.groovyObject?.getProperty("buildToolsVersion") as? String)?.substringBefore(
                '.'
            )
                ?: (project.extensions.findByName("android")?.groovyObject?.getProperty("compileSdkVersion") as? String)?.substringBefore(
                    '.'
                )

        project.afterEvaluate {
            println("Determined your package to be ${packageName()}")
        }

        //IOS

        project.tasks.create("khrysalisConvertKotlinToSwift") { task ->
            task.group = "ios"
            var compileTask: KotlinCompile? = null
            project.afterEvaluate {
                compileTask = project.tasks
                    .asSequence()
                    .filter { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }
                    .mapNotNull { it as? KotlinCompile }
                    .minBy { it.name.length }
                compileTask?.let {
                    println("Conversion depends on ${it.name}")
                    task.dependsOn(it)
                } ?: run {
                    println("WARNING: Could find no compile*Kotlin tasks - tasks available: ${project.tasks.joinToString { it.name }}")
                }
            }
            task.doFirst {
                val originalTask = compileTask
                    ?: project.tasks
                        .asSequence()
                        .filter { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }
                        .mapNotNull { it as? KotlinCompile }
                        .minBy { it.name.length }
                    ?: throw IllegalStateException("Could not find compile*Kotlin tasks - what's up with your project?")
                val libraries = originalTask.classpath.asSequence()
                val files = originalTask.source.toList().asSequence()
                println("All files: ${files.joinToString("\n")}")
                println("All libraries: ${libraries.joinToString("\n")}")
                convertToSwift(
                    projectName = projectName(),
                    libraries = libraries,
                    files = files,
                    pluginCache = project.buildDir.resolve("khrysalis-kcp"),
                    buildCache = project.buildDir.resolve("testBuild"),
                    dependencies = run {
                        val localProperties = Properties().apply {
                            val f = project.rootProject.file("local.properties")
                            if (f.exists()) {
                                load(f.inputStream())
                            }
                        }
                        val pathRegex = Regex(":path => '([^']+)'")
                        val home = System.getProperty("user.home")
                        val localPodSpecRefs = iosBase()
                            .resolve("Podfile")
                            .takeIf { it.exists() }
                            ?.also { println("Found podfile: $it") }
                            ?.let {file ->
                                file
                                    .readText()
                                    .let { pathRegex.findAll(it) }
                                    .also { println("Found podfile paths: ${it.joinToString{ it.value }}") }
                                    .map { it.groupValues[1] }
                                    .map { it.replace("~", home) }
                                    .map {
                                        if(it.startsWith('/'))
                                            File(it).parentFile
                                        else
                                            File(file.parentFile, it).parentFile
                                    }
                            } ?: sequenceOf()
                        val allLocations = (localProperties.getProperty("khrysalis.iospods")
                            ?: localProperties.getProperty("khrysalis.nonmacmanifest") ?: "")
                            .splitToSequence(File.pathSeparatorChar)
                            .filter { it.isNotBlank() }
                            .map { File(it) }
                            .filter { it.exists() }
                            .plus(sequenceOf(iosBase()))
                            .plus(sequenceOf(androidBase()))
                            .plus(localPodSpecRefs)
                        println("Checking for equivalents at: ${allLocations.joinToString("\n")}")
                        allLocations
                    },
                    output = iosFolder().resolve("src")
                )
            }
        }
        project.tasks.create("khrysalisUpdateIosVersion") { task ->
            task.group = "ios"
            task.doLast {
                val versionName = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionName") as? String ?: throw IllegalStateException("Could not find versionName")
                val versionCode = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionCode") as? Int ?: throw IllegalStateException("Could not find versionCode")
                val projectFile = (iosBase().listFiles()?.toList()
                    ?.find { it.name.endsWith("xcodeproj", true) }
                    ?: throw IllegalStateException("Could not find projectFile at ${iosBase()}"))
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
        project.tasks.create("khrysalisIos") { task ->
            task.group = "ios"
            task.dependsOn("khrysalisConvertKotlinToSwift")
            task.dependsOn("khrysalisConvertLayoutsToSwift")
            task.dependsOn("khrysalisUpdateIosVersion")
            if (isMac) {
                task.finalizedBy("khrysalisIosUpdateFiles")
            }
        }
        if (isMac) {
            project.tasks.create("khrysalisIosUpdateFiles", Exec::class.java) { task ->
                task.group = "ios"
                task.commandLine = listOf("python3", "updateFiles.py")
                task.doFirst {
                    task.workingDir = iosBase()
                }
            }
        }


        //Web
        project.tasks.create("khrysalisUpdateWebVersion") { task ->
            task.group = "web"
            task.doLast {
                val versionName = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionName") as? String ?: throw IllegalStateException("Could not find versionName")
                val versionCode = project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                    ?.getProperty("versionCode") as? Int ?: throw IllegalStateException("Could not find versionCode")
                webBase().resolve("src/BuildConfig.ts").writeText("""
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
        project.tasks.create("khrysalisConvertKotlinToTypescript") { task ->
            task.dependsOn("khrysalisUpdateWebVersion")
            task.group = "web"
            var compileTask: KotlinCompile? = null
            project.afterEvaluate {
                compileTask = project.tasks
                    .asSequence()
                    .filter { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }
                    .mapNotNull { it as? KotlinCompile }
                    .minBy { it.name.length }
                compileTask?.let {
                    task.dependsOn(it)
                }
            }
            task.doFirst {
                val originalTask = compileTask
                    ?: project.tasks
                        .asSequence()
                        .filter { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }
                        .mapNotNull { it as? KotlinCompile }
                        .minBy { it.name.length }
                    ?: throw IllegalStateException("Could not find compile*Kotlin tasks - what's up with your project?")
                val libraries = originalTask.classpath.asSequence()
                val files = originalTask.source.toList().asSequence()
                println("All files: ${files.joinToString("\n")}")
                println("All libraries: ${libraries.joinToString("\n")}")
                convertToTypescript(
                    projectName = projectName(),
                    libraries = libraries,
                    files = files,
                    pluginCache = project.buildDir.resolve("khrysalis-kcp"),
                    buildCache = project.buildDir.resolve("testBuild"),
                    dependencies = sequenceOf(webBase()),
                    output = webBase().resolve("src")
                )
            }
        }
        project.tasks.create("khrysalisWeb") { task ->
            task.group = "web"
            task.dependsOn("khrysalisConvertKotlinToTypescript")
        }

    }
}

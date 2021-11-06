package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.generic.CompilerRunInfo
import com.lightningkite.khrysalis.generic.runCompiler
import com.lightningkite.khrysalis.ios.swift.*
import com.lightningkite.khrysalis.kotlin.kotlinPluginUse
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.typescriptPluginUse
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

        fun Task.getKotlinCompileTask(): KotlinCompile? {
            return project.tasks
                .asSequence()
                .filter { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }
                .mapNotNull { it as? KotlinCompile }
                .minBy { it.name.length }
        }
        fun Task.dependOnKotlinCompileTask() {
            project.afterEvaluate {
                val compileTask = getKotlinCompileTask()
                compileTask?.let {
                    println("Conversion depends on ${it.name}")
                    this.dependsOn(it)
                } ?: run {
                    println("WARNING: Could find no compile*Kotlin tasks - tasks available: ${project.tasks.joinToString { it.name }}")
                }
            }
        }

        project.afterEvaluate {
            println("Determined your package to be ${packageName()}")
        }

        project.tasks.create("khrysalisPrintDeclaredFqns") { task ->
            task.group = "kotlin"
            task.dependOnKotlinCompileTask()
            task.doFirst {
                runCompiler(
                    CompilerRunInfo(
                        task.getKotlinCompileTask()
                            ?: throw IllegalStateException("Could not find compile*Kotlin tasks - what's up with your project?")
                    ),
                    kotlinPluginUse(project)
                )
            }
        }

        //IOS

        project.tasks.create("khrysalisConvertKotlinToSwift") { task ->
            task.group = "ios"
            task.dependOnKotlinCompileTask()
            task.doFirst {
                runCompiler(
                    CompilerRunInfo(
                        task.getKotlinCompileTask()
                            ?: throw IllegalStateException("Could not find compile*Kotlin tasks - what's up with your project?")
                    ),
                    swiftPluginUse(project, iosBase(), projectName())
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
            task.dependOnKotlinCompileTask()
            task.doFirst {
                runCompiler(
                    CompilerRunInfo(
                        task.getKotlinCompileTask()
                            ?: throw IllegalStateException("Could not find compile*Kotlin tasks - what's up with your project?")
                    ),
                    typescriptPluginUse(project, webBase(), projectName())
                )
            }
        }
        project.tasks.create("khrysalisWeb") { task ->
            task.group = "web"
            task.dependsOn("khrysalisConvertKotlinToTypescript")
        }

    }
}

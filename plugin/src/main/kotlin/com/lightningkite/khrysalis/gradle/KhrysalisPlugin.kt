package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.generic.CompilerPluginUseInfo
import com.lightningkite.khrysalis.generic.CompilerRunInfo
import com.lightningkite.khrysalis.generic.runCompiler
import com.lightningkite.khrysalis.ios.swift.*
import com.lightningkite.khrysalis.utils.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.*
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.Jar
import java.io.File
import java.util.*

open class KhrysalisPluginExtension {
    open var organizationName: String = "Organization"
    open var iosProjectName: String? = null
    open var iosSourceFolder: File? = null
    open var iosProjectFolder: File? = null
    open var webProjectName: String? = null
    open var webSourceFolder: File? = null
    open var webProjectFolder: File? = null
    open var libraryMode: Boolean = false

    @Deprecated("Use iosProjectName instead")
    open var projectName: String? = null
    @Deprecated("Use iosProjectFolder instead")
    open var overrideIosFolder: File? = null
    @Deprecated("Use webProjectFolder instead")
    open var overrideWebFolder: File? = null

    private var completed: KhrysalisExtensionSettings? = null
    fun complete(project: Project): KhrysalisExtensionSettings = completed ?: run {
        val libraryMode = this.libraryMode
        val organizationName = this.organizationName
        val iosProjectName =
            this.iosProjectName ?: this.projectName ?: project.name.takeUnless { it == "app" || it == "android" }
            ?: project.rootProject.name
        val webProjectName =
            this.webProjectName ?: this.projectName ?: project.name.takeUnless { it == "app" || it == "android" }
            ?: project.rootProject.name
        val iosProjectFolder = this.iosProjectFolder ?: this.overrideIosFolder ?: project.projectDir.resolve("../ios")
        val iosSourceFolder = this.iosSourceFolder ?: (if (libraryMode) iosProjectFolder.resolve(iosProjectName)
            .resolve("Classes") else iosProjectFolder.resolve(iosProjectName).resolve("src"))
        val webProjectFolder = this.webProjectFolder ?: this.overrideWebFolder ?: project.projectDir.resolve("../web")
        val webSourceFolder = this.webSourceFolder ?: webProjectFolder.resolve("src")
        val result = KhrysalisExtensionSettings(
            organizationName = organizationName,
            iosProjectName = iosProjectName,
            webProjectName = webProjectName,
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
    val iosProjectName: String,
    val webProjectName: String,
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

val Project.khrysalis: KhrysalisExtensionSettings
    get() = (project.extensions.getByName("khrysalis") as KhrysalisPluginExtension).complete(
        this
    )

fun KotlinCompile.calculateCommonPackage(): String {
    return sources
        .files
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
        val extension =
            project.extensions.create<KhrysalisPluginExtension>("khrysalis", KhrysalisPluginExtension::class.java)

        val equivalentsConfiguration = project.configurations.maybeCreate("equivalents").apply {
            description = "Equivalent declarations for translations"
            isCanBeResolved = true
            isCanBeConsumed = true
            isVisible = true
        }

        project.configurations.maybeCreate("kcp").apply {
            description = "Kotlin compiler plugin dependencies"
            isCanBeResolved = true
            isCanBeConsumed = false
            isVisible = true
        }

        KhrysalisSettings.verbose = true
        val webBase by lazy { target.khrysalis.webProjectFolder }
        val webSrc by lazy { target.khrysalis.webSourceFolder }
        val iosBase by lazy { target.khrysalis.iosProjectFolder }
        val iosSrc by lazy { target.khrysalis.iosSourceFolder }


        val equivalentDirectorySet = target.objects.sourceDirectorySet("equivalents", "Khrysalis Equivalents")
        equivalentDirectorySet.srcDirs(target.projectDir.resolve("src/main/equivalents"))
        val equivalentsJarTask = project.tasks.create("equivalentsJar", Jar::class.java) { task ->
            task.group = "khrysalis"
            task.archiveClassifier.set("equivalents")
            task.from(equivalentDirectorySet)
        }
        val equivalentsJarSourcesTask = project.tasks.create("equivalentsJarSources") { task ->
            task.group = "khrysalis"
            task.doLast {
                equivalentsJarTask.source.forEach {
                    println(it)
                }
            }
        }

        project.artifacts {
            it.add("equivalents", equivalentsJarTask)
        }

        project.afterEvaluate {
            project.tasks.filterIsInstance<KotlinCompile>().forEach { original ->
                val sourceSetName = "main"
                val equivalentSourceFolder = project.projectDir.resolve("src/${sourceSetName}/equivalents")

                project.tasks.create("${original.name}ToSwift") { task ->
                    task.group = "ios"
                    task.dependsOn(project.configurations.getByName("kcp"))
                    task.dependsOn(original)
                    task.doFirst {
                        val fqNameFile = equivalentSourceFolder.resolve("swift.fqnames")
                        val equivalentJars = equivalentsConfiguration.toList()
                        runCompiler(
                            CompilerRunInfo(original),
                            CompilerPluginUseInfo.make(
                                project = project,
                                pluginName = "swift",
                                options = mapOf(
                                    "outputDirectory" to iosSrc.absolutePath,
                                    "outputFqnames" to fqNameFile.absolutePath,
                                    "projName" to target.khrysalis.iosProjectName,
                                    "equivalents" to (equivalentJars + equivalentDirectorySet.toList()).joinToString(File.pathSeparator),
                                    "commonPackage" to original.calculateCommonPackage(),
                                    "libraryMode" to extension.libraryMode.toString(),
                                )
                            )
                        )
                    }
                }
                project.tasks.create("${original.name}ToTypescript") { task ->
                    task.group = "web"
                    task.dependsOn(project.configurations.getByName("kcp"))
                    task.dependsOn(original)
                    task.doFirst {
                        val equivalentJars = equivalentsConfiguration.toList()
                        val fqNameFile = equivalentSourceFolder.resolve("ts.fqnames")
                        runCompiler(
                            CompilerRunInfo(original),
                            CompilerPluginUseInfo.make(
                                project = project,
                                pluginName = "typescript",
                                options = mapOf(
                                    "outputDirectory" to webSrc.absolutePath,
                                    "outputFqnames" to fqNameFile.absolutePath,
                                    "projName" to target.khrysalis.webProjectName,
                                    "equivalents" to (equivalentJars + equivalentDirectorySet.toList()).joinToString(File.pathSeparator),
                                    "commonPackage" to original.calculateCommonPackage(),
                                    "libraryMode" to extension.libraryMode.toString(),
                                )
                            )
                        )
                    }
                }
            }
        }

        project.tasks.create("listEquivalentJars") { task ->
            task.group = "khrysalis"
            task.doLast {
                println("--- Equivalent JARs ---")
                for (file in equivalentsConfiguration.toList()) {
                    println(file)
                }
                println("--- Equivalent Directories ---")
                println(equivalentDirectorySet.joinToString())
            }
        }

        project.tasks.create("listEquivalentFiles") { task ->
            task.group = "khrysalis"
            task.doLast {
                println("--- Equivalent Files ---")
                equivalentsConfiguration.toList()
                    .asSequence()
                    .plus(equivalentDirectorySet.toList())
                    .flatMap { it.walkZip() }
                    .filter { it.name.endsWith(".yaml") || it.name.endsWith(".fqnames") }
                    .groupBy { it.name.substringBeforeLast('.').substringAfterLast('.') }
                    .forEach {
                        println("--- ${it.key} ---")
                        it.value.forEach {
                            println(it)
                        }
                    }
            }
        }

        project.tasks.create("updateIosVersion") { task ->
            task.group = "ios"
            task.doLast {
                val versionName =
                    project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                        ?.getProperty("versionName") as? String ?: project.version.toString()
                val versionCode =
                    project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                        ?.getProperty("versionCode") as? Int ?: 0
                val projectFile = (iosBase.listFiles()?.toList()
                    ?.find { it.name.endsWith("xcodeproj", true) }
                    ?: throw IllegalStateException("Could not find projectFile at ${iosBase}"))
                    .resolve("project.pbxproj")
                    .also {
                        if (!it.exists()) {
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
                val versionName =
                    project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                        ?.getProperty("versionName") as? String ?: project.version.toString()
                val versionCode =
                    project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                        ?.getProperty("versionCode") as? Int ?: 0
                val androidPackageName =
                    project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                        ?.getProperty("applicationId") as? String ?: "com.test"
                val projectFile = webBase.resolve("package.json")
                projectFile.readText()
                    .replace(Regex(""""version": "([0-9.]+)""""), """"version": "$versionName"""")
                    .let { projectFile.writeText(it) }
                webBase.resolve("src/BuildConfig.ts").writeText(
                    """
                    //! Declares ${androidPackageName}.BuildConfig
                    export class BuildConfig {
                        static INSTANCE = BuildConfig
                        static VERSION_NAME: string = "$versionName"
                        static VERSION_CODE: number = $versionCode
                        static get DEBUG(): boolean {
                            return (window as any).isDebugMode ?? false
                        }
                    }
                """.trimIndent()
                )
            }
        }
    }
}

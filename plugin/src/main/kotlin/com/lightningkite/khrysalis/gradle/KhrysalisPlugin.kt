package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.android.layout.createAndroidLayoutClasses
import com.lightningkite.khrysalis.flow.createFlowDocumentation
import com.lightningkite.khrysalis.flow.createPrototypeViewGenerators
import com.lightningkite.khrysalis.ios.layout.*
import com.lightningkite.khrysalis.ios.*
import com.lightningkite.khrysalis.ios.swift.*
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.convertToTypescript
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import com.lightningkite.khrysalis.web.layout.convertLayoutsToHtml
import com.lightningkite.khrysalis.web.setUpWebProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.lightningkite.khrysalis.web.layout.convertLayoutsToHtmlXmlClasses
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Task
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File
import java.util.*

open class KhrysalisPluginExtension {
    open var organizationName: String = "Organization"
    open var swiftLayoutConversion: LayoutConverter = LayoutConverter.normal
    open var htmlTranslator: HtmlTranslator = HtmlTranslator()
    open var projectName: String? = null
    open var overrideIosPackageName: String? = null
    open var overrideWebPackageName: String? = null
    open var overrideIosFolder: String? = null
    open var overrideWebFolder: String? = null

    override fun toString(): String {
        return "(" +
                "\norganizationName: " + organizationName +
                "\nswiftLayoutConversion: " + swiftLayoutConversion +
                "\nhtmlTranslator: " + htmlTranslator +
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
            project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
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


        //Android support

        project.tasks.create("khrysalisAndroid") { task ->
            task.group = "android"
            task.dependsOn("khrysalisCreateAndroidLayoutClasses")
        }
        project.tasks.create("khrysalisCreateAndroidLayoutClasses") { task ->
            task.group = "android"
            task.doLast {
                createAndroidLayoutClasses(
                    androidFolder = androidBase(),
                    applicationPackage = packageName()
                )
            }
        }
        project.afterEvaluate {
            project.tasks.findByName("generateReleaseResources")?.dependsOn("khrysalisAndroid") ?: run {
                println("Could not configure resource dependency.  You'll need to run 'khrysalisAndroid' manually.")
            }
            project.tasks.findByName("generateDebugResources")?.dependsOn("khrysalisAndroid") ?: run {
                println("Could not configure resource dependency.  You'll need to run 'khrysalisAndroid' manually.")
            }
        }

        //IOS

        project.tasks.create("khrysalisSetupIosProject") { task ->
            task.group = "ios"
            task.doLast {
                setUpIosProject(
                    target = iosBase(),
                    organization = extension().organizationName,
                    organizationId = extension().overrideIosPackageName ?: project.group?.toString()
                    ?: "unknown.packagename",
                    projectName = projectName()
                )
            }
            if (isMac) {
                task.finalizedBy("khrysalisIosPodInstall")
            }
        }
        if (isMac) {
            project.tasks.create("khrysalisIosPodInstall", Exec::class.java) { task ->
                task.group = "ios"
                task.commandLine = listOf("pod", "install")
                task.doFirst {
                    task.workingDir = iosBase()
                }
            }
        }
        project.tasks.create("khrysalisConvertKotlinToSwift") { task ->
            task.group = "ios"
            task.dependsOn("compileDebugKotlin")
            task.doFirst {
                val originalTask = project.tasks.getByName("compileDebugKotlin") as KotlinCompile
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
                            if(f.exists()){
                                load(f.inputStream())
                            }
                        }
                        (localProperties.getProperty("khrysalis.iospods") ?:
                        localProperties.getProperty("khrysalis.nonmacmanifest") ?: "")
                            .splitToSequence(File.pathSeparatorChar)
                            .filter { it.isNotBlank() }
                            .map { File(it) }
                            .filter { it.exists() }
                            .plus(sequenceOf(iosBase().resolve("Pods")))
                    },
                    output = iosFolder().resolve("src")
                )
            }
        }
        project.tasks.create("khrysalisConvertLayoutsToSwift") { task ->
            task.group = "ios"
            task.doLast {

                convertLayoutsToSwift(
                    androidFolder = androidBase(),
                    iosFolder = iosFolder(),
                    converter = extension().swiftLayoutConversion
                )

            }
        }
        project.tasks.create("khrysalisConvertResourcesToIos") { task ->
            task.group = "ios"
            task.doLast {

                convertResourcesToIos(
                    androidFolder = androidBase(),
                    iosFolder = iosFolder()
                )

            }
        }
        project.tasks.create("khrysalisIos") { task ->
            task.group = "ios"
            task.dependsOn("khrysalisConvertKotlinToSwift")
            task.dependsOn("khrysalisConvertLayoutsToSwift")
            task.dependsOn("khrysalisConvertResourcesToIos")
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
        project.tasks.create("khrysalisSetupWebProject") { task ->
            task.group = "web"
            task.doLast {
                setUpWebProject(
                    target = webBase(),
                    organization = extension().organizationName,
                    organizationId = project.group?.toString() ?: "unknown.packagename",
                    projectName = projectName()
                )
            }
            task.finalizedBy("khrysalisWebNpmInstall")
        }
        project.tasks.create("khrysalisWebNpmInstall", Exec::class.java) { task ->
            task.group = "web"
            task.commandLine = listOf("npm", "install")
            task.doFirst {
                task.workingDir = webBase()
            }
        }
        project.tasks.create("khrysalisConvertLayoutsToHtmlSass") { task ->
            task.dependsOn("khrysalisCreateAndroidLayoutClasses")
            project.afterEvaluate {
                if (!iosFolder().exists()) {
                    task.dependsOn("khrysalisSetupWebProject")
                }
            }
            task.group = "web"
            task.doLast {
                convertLayoutsToHtml(
                    androidMainFolder = androidBase().resolve("src/main"),
                    webFolder = webBase(),
                    packageName = packageName(),
                    converter = extension().htmlTranslator
                )
                convertLayoutsToHtmlXmlClasses(
                    projectName = projectName(),
                    packageName = packageName(),
                    androidLayoutsSummaryFile = androidBase().resolve("build/layout/summary.json"),
                    baseTypescriptFolder = webBase().resolve("src"),
                    outputFolder = webBase().resolve("src/layout")
                )
            }
        }
        project.tasks.create("khrysalisConvertKotlinToTypescript") { task ->
            task.group = "web"
            task.dependsOn("compileDebugKotlin")
            task.doFirst {
                val originalTask = project.tasks.getByName("compileDebugKotlin") as KotlinCompile
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
            task.dependsOn("khrysalisConvertLayoutsToHtmlSass")
            task.dependsOn("khrysalisConvertKotlinToTypescript")
        }


        //Prototyping

        project.tasks.create("khrysalisPrototype") { task ->
            task.group = "prototype"
            task.dependsOn("khrysalisCreateAndroidLayoutClasses")
            task.doLast {

                createPrototypeViewGenerators(
                    androidFolder = androidBase(),
                    applicationPackage = packageName()
                )

            }
        }
        project.tasks.create("khrysalisFlowDoc") { task ->
            task.group = "prototype"
            task.dependsOn("khrysalisCreateAndroidLayoutClasses")
            task.doLast {
                createFlowDocumentation(
                    androidFolder = androidBase()
                )
            }
        }

    }
}

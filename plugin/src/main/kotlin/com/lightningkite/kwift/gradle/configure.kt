package com.lightningkite.kwift.gradle

import com.lightningkite.kwift.KwiftSettings
import com.lightningkite.kwift.convertResourcesToIos
import com.lightningkite.kwift.flow.createFlowDocumentation
import com.lightningkite.kwift.flow.createPrototypeViewGenerators
import com.lightningkite.kwift.layout.convertLayoutsToSwift
import com.lightningkite.kwift.layout.createAndroidLayoutClasses
import com.lightningkite.kwift.log
import com.lightningkite.kwift.swift.SwiftAltListener
import com.lightningkite.kwift.swift.convertKotlinToSwift
import org.gradle.api.Project
import java.io.File

fun Project.convertKotlinToSwiftWithDependencies(
    androidFolder: File,
    iosFolder: File,
    clean: Boolean = false,
    setup: SwiftAltListener.() -> Unit = {}
) {
    convertKotlinToSwift(
        androidFolder = androidFolder,
        iosFolder = iosFolder,
        clean = clean,
        setup = {
            configurations
                .asSequence()
                .flatMap {
                    if (it.isCanBeResolved) it.resolve().asSequence() else sequenceOf()
                }
                .flatMap { archive ->

                    if (archive.extension == "aar") {
                        zipTree(archive)
                            .matching {
                                it.include("classes.jar")
                            }
                            .asSequence()
                    } else sequenceOf()
                }
                .flatMap {
                    zipTree(it)
                        .matching { it.include("META-INF/kwift-interfaces.json") }
                        .asSequence()
//                        .filter { it.name == "kwift-interfaces.json" }
                }
                .distinct()
                .forEach {
                    log("Loading interfaces from $it...")
                    loadInterfaces(it)
                }
            setup()
        }
    )
}

fun Project.configureGradle(iosRelativeBase: String = "../../ios/Klyp") {

    KwiftSettings.verbose = true
    val androidBase = project.projectDir
    val iosBase = project.projectDir.resolve(iosRelativeBase)

    tasks.create("kwiftConvertKotlinToSwiftClean") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            convertKotlinToSwiftWithDependencies(
                androidFolder = androidBase,
                iosFolder = iosBase,
                clean = true
            ) {
                imports = listOf("Kwift")
            }
            println("Finished")
        }
    }
    tasks.create("kwiftConvertKotlinToSwift") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            convertKotlinToSwiftWithDependencies(
                androidFolder = androidBase,
                iosFolder = iosBase,
                clean = false
            ) {
                imports = listOf("Kwift")
            }
            println("Finished")
        }
    }
    tasks.create("kwiftCreateAndroidLayoutClasses") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            createAndroidLayoutClasses(
                androidFolder = androidBase,
                applicationPackage = "com.klypme"
            )
            println("Finished")
        }
    }
    tasks.create("kwiftConvertLayoutsToSwift") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            convertLayoutsToSwift(
                androidFolder = androidBase,
                iosFolder = iosBase
            )
            println("Finished")
        }
    }
    tasks.create("kwiftConvertResourcesToIos") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            convertResourcesToIos(
                androidFolder = androidBase,
                iosFolder = iosBase
            )
            println("Finished")
        }
    }
    tasks.create("kwiftIos") { task ->
        task.group = "build"
        task.dependsOn("kwiftConvertKotlinToSwift")
        task.dependsOn("kwiftConvertLayoutsToSwift")
        task.dependsOn("kwiftConvertResourcesToIos")
        task.dependsOn("kwiftCreateAndroidLayoutClasses")
    }

    tasks.create("kwiftPrototype") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            createPrototypeViewGenerators(
                androidFolder = androidBase,
                applicationPackage = "com.klypme"
            )
            println("Finished")
        }
    }

    tasks.create("kwiftFlowDoc") { task ->
        task.group = "build"
        task.doLast {
            println("Started")
            createFlowDocumentation(
                androidFolder = androidBase
            )
            println("Finished")
        }
    }

}

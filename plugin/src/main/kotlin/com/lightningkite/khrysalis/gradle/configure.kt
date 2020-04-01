package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.convertResourcesToIos
import com.lightningkite.khrysalis.flow.createFlowDocumentation
import com.lightningkite.khrysalis.flow.createPrototypeViewGenerators
import com.lightningkite.khrysalis.layout.LayoutConverter
import com.lightningkite.khrysalis.layout.convertLayoutsToSwift
import com.lightningkite.khrysalis.layout.createAndroidLayoutClasses
import com.lightningkite.khrysalis.layout.normal
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.swift.SwiftAltListener
import com.lightningkite.khrysalis.swift.convertKotlinToSwift
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
        webFolder = iosFolder,
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
                        .matching { it.include("META-INF/khrysalis-interfaces.json") }
                        .asSequence()
//                        .filter { it.name == "khrysalis-interfaces.json" }
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

fun Project.configureGradle(
    packageName: String,
    iosRelativeBase: String,
    setupCodeConversion: SwiftAltListener.() -> Unit = {},
    setupLayoutConversion: LayoutConverter = LayoutConverter.normal
) {

    KhrysalisSettings.verbose = true
    val androidBase = project.projectDir
    val iosBase = project.projectDir.resolve(iosRelativeBase)

    tasks.create("khrysalisConvertKotlinToSwiftClean") { task ->
        task.group = "build"
        task.doLast {

            convertKotlinToSwiftWithDependencies(
                androidFolder = androidBase,
                iosFolder = iosBase,
                clean = true
            ) {
                imports = listOf("Khrysalis", "RxSwift", "RxRelay")
                setupCodeConversion()
            }

        }
    }
    tasks.create("khrysalisConvertKotlinToSwift") { task ->
        task.group = "build"
        task.doLast {

            convertKotlinToSwiftWithDependencies(
                androidFolder = androidBase,
                iosFolder = iosBase,
                clean = false
            ) {
                imports = listOf("Khrysalis", "RxSwift", "RxRelay")
                setupCodeConversion()
            }

        }
    }
    tasks.create("khrysalisCreateAndroidLayoutClasses") { task ->
        task.group = "build"
        task.doLast {

            createAndroidLayoutClasses(
                androidFolder = androidBase,
                applicationPackage = packageName
            )

        }
    }
    tasks.create("khrysalisConvertLayoutsToSwift") { task ->
        task.group = "build"
        task.doLast {

            convertLayoutsToSwift(
                androidFolder = androidBase,
                iosFolder = iosBase,
                converter = setupLayoutConversion
            )

        }
    }
    tasks.create("khrysalisConvertResourcesToIos") { task ->
        task.group = "build"
        task.doLast {

            convertResourcesToIos(
                androidFolder = androidBase,
                iosFolder = iosBase
            )

        }
    }
    tasks.create("khrysalisIos") { task ->
        task.group = "build"
        task.dependsOn("khrysalisConvertKotlinToSwift")
        task.dependsOn("khrysalisConvertLayoutsToSwift")
        task.dependsOn("khrysalisConvertResourcesToIos")
        task.dependsOn("khrysalisCreateAndroidLayoutClasses")
    }

    tasks.create("khrysalisPrototype") { task ->
        task.group = "build"
        task.doLast {

            createPrototypeViewGenerators(
                androidFolder = androidBase,
                applicationPackage = packageName
            )

        }
    }

    tasks.create("khrysalisFlowDoc") { task ->
        task.group = "build"
        task.doLast {

            createFlowDocumentation(
                androidFolder = androidBase
            )

        }
    }

}

package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.ios.convertResourcesToIos
import com.lightningkite.khrysalis.flow.createFlowDocumentation
import com.lightningkite.khrysalis.flow.createPrototypeViewGenerators
import com.lightningkite.khrysalis.ios.layout.LayoutConverter
import com.lightningkite.khrysalis.ios.layout.convertLayoutsToSwift
import com.lightningkite.khrysalis.ios.layout.createAndroidLayoutClasses
import com.lightningkite.khrysalis.ios.layout.normal
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.ios.swift.SwiftAltListener
import com.lightningkite.khrysalis.ios.swift.convertKotlinToSwift
import com.lightningkite.khrysalis.preparse.PreparseData
import com.lightningkite.khrysalis.preparse.plus
import com.lightningkite.khrysalis.web.typescript.TypescriptTranslator
import com.lightningkite.khrysalis.web.typescript.convertKotlinToTypescript
import org.gradle.api.Project
import java.io.File

fun Project.convertKotlinToTypescriptWithDependencies(
    androidFolder: File,
    webFolder: File,
    clean: Boolean = false,
    translator: TypescriptTranslator
) {
    convertKotlinToTypescript(
        androidMainFolder = androidFolder.resolve("src/main"),
        webFolder = webFolder,
        clean = clean,
        typescript = translator.apply{
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
                }
                .distinct()
                .forEach {
                    log("Loading interfaces from $it...")
                    this.preparseData += PreparseData.from(it)
                }
        }
    )
}

fun Project.configureGradleWeb(
    packageName: String,
    webRelativeBase: String,
    setupCodeConversion: SwiftAltListener.() -> Unit = {},
    setupLayoutConversion: LayoutConverter = LayoutConverter.normal
) {
//
//    KhrysalisSettings.verbose = true
//    val androidBase = project.projectDir
//    val iosBase = project.projectDir.resolve(iosRelativeBase)
//
//    tasks.create("khrysalisConvertKotlinToSwiftClean") { task ->
//        task.group = "ios"
//        task.doLast {
//
//            convertKotlinToSwiftWithDependencies(
//                androidFolder = androidBase,
//                iosFolder = iosBase,
//                clean = true
//            ) {
//                imports = listOf("Khrysalis", "RxSwift", "RxRelay")
//                setupCodeConversion()
//            }
//
//        }
//    }
//    tasks.create("khrysalisConvertKotlinToSwift") { task ->
//        task.group = "ios"
//        task.doLast {
//
//            convertKotlinToSwiftWithDependencies(
//                androidFolder = androidBase,
//                iosFolder = iosBase,
//                clean = false
//            ) {
//                imports = listOf("Khrysalis", "RxSwift", "RxRelay")
//                setupCodeConversion()
//            }
//
//        }
//    }
//    tasks.create("khrysalisCreateAndroidLayoutClasses") { task ->
//        task.group = "build"
//        task.doLast {
//
//            createAndroidLayoutClasses(
//                androidFolder = androidBase,
//                applicationPackage = packageName
//            )
//
//        }
//    }
//    tasks.create("khrysalisConvertLayoutsToSwift") { task ->
//        task.group = "ios"
//        task.doLast {
//
//            convertLayoutsToSwift(
//                androidFolder = androidBase,
//                iosFolder = iosBase,
//                converter = setupLayoutConversion
//            )
//
//        }
//    }
//    tasks.create("khrysalisConvertResourcesToIos") { task ->
//        task.group = "ios"
//        task.doLast {
//
//            convertResourcesToIos(
//                androidFolder = androidBase,
//                iosFolder = iosBase
//            )
//
//        }
//    }
//    tasks.create("khrysalisIos") { task ->
//        task.group = "ios"
//        task.dependsOn("khrysalisConvertKotlinToSwift")
//        task.dependsOn("khrysalisConvertLayoutsToSwift")
//        task.dependsOn("khrysalisConvertResourcesToIos")
//        task.dependsOn("khrysalisCreateAndroidLayoutClasses")
//    }
//
//    tasks.create("khrysalisPrototype") { task ->
//        task.group = "build"
//        task.doLast {
//
//            createPrototypeViewGenerators(
//                androidFolder = androidBase,
//                applicationPackage = packageName
//            )
//
//        }
//    }
//
//    tasks.create("khrysalisFlowDoc") { task ->
//        task.group = "build"
//        task.doLast {
//
//            createFlowDocumentation(
//                androidFolder = androidBase
//            )
//
//        }
//    }

}

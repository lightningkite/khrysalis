package com.lightningkite.kwift

import com.lightningkite.kwift.altswift.convertKotlinToSwift2
import com.lightningkite.kwift.layoutxml.convertAndroidResourcesToSwift
import com.lightningkite.kwift.layoutxml.createAndroidLayoutClasses
import com.lightningkite.kwift.swift.convertKotlinToSwift
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.kotlin.KotlinLexer
import java.io.File


const val INTERFACE_SCAN_VERSION: Int = 2
const val VERSION: Int = 10

fun main(vararg args: String) {
    convertKotlinToSwift(
        File("/Users/josephivie/IdeaProjects/kwift/testData"),
        File("/Users/josephivie/IdeaProjects/kwift/testDataOutput")
    )
    convertKotlinToSwift2(
        File("/Users/josephivie/IdeaProjects/kwift/testData"),
        File("/Users/josephivie/IdeaProjects/kwift/testDataOutput2")
    )
    createAndroidLayoutClasses(
        resourcesFolder = File("/Users/josephivie/IdeaProjects/kwift/testData/res"),
        applicationPackage = "com.lightningkite.kwifttest",
        outputFolder = File("/Users/josephivie/IdeaProjects/kwift/testDataOutput/res")
    )
    convertAndroidResourcesToSwift(
        resourcesFolder = File("/Users/josephivie/IdeaProjects/kwift/testData/res"),
        baseFolderForLocalizations = File("/Users/josephivie/IdeaProjects/kwift/testDataOutput/ios/locales"),
        outputFolder = File("/Users/josephivie/IdeaProjects/kwift/testDataOutput/ios")
    )
}

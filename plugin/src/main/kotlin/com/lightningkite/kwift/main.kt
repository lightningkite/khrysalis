package com.lightningkite.kwift

import com.lightningkite.kwift.layout.convertLayoutsToSwift
import com.lightningkite.kwift.layout.createAndroidLayoutClasses
import com.lightningkite.kwift.swift.convertKotlinToSwift
import com.lightningkite.kwift.swift.convertKotlinToSwiftByFolder
import java.io.File


const val INTERFACE_SCAN_VERSION: Int = 2
const val VERSION: Int = 12

fun main(vararg args: String) {
    println("Checking ${File("./testData").walkTopDown()
        .filter { it.extension == "kt" }
        .filter { it.name.contains(".shared") }.toList()}")
    convertKotlinToSwiftByFolder(
        interfacesOut = File("./testDataOutput/interfaces.json"),
        baseKotlin = File("./testData"),
        baseSwift = File("./testDataOutput"),
        clean = true
    )
//    convertKotlinToSwift(
//        androidFolder = File("/Users/josephivie/StudioProjects/kwift-template/android/app"),
//        iosFolder = File("/Users/josephivie/StudioProjects/kwift-template/ios/Kwift Template"),
//        clean = true
//    )
//    createAndroidLayoutClasses(
//        androidFolder = File("/Users/josephivie/StudioProjects/kwift-template/android/app"),
//        applicationPackage = "com.lightningkite.kwifttest"
//    )
//    convertLayoutsToSwift(
//        androidFolder = File("/Users/josephivie/StudioProjects/kwift-template/android/app"),
//        iosFolder = File("/Users/josephivie/StudioProjects/kwift-template/ios/Kwift Template")
//    )
//    convertResourcesToIos(
//        androidFolder = File("/Users/josephivie/StudioProjects/kwift-template/android/app"),
//        iosFolder = File("/Users/josephivie/StudioProjects/kwift-template/ios/Kwift Template")
//    )
}

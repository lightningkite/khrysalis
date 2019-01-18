package com.lightningkite.kwift

import java.io.File


fun main(vararg args: String) {
    println("Groan...")
    kwiftTask(
            directory = File("/Users/josephivie/StudioProjects/penny-profit-android/app/src/main/java/com/lightningkite/pennyprofit/shared"),
            outputDirectory = File("/Users/josephivie/Documents/GitHub/penny-profit-ios/PennyProfit/shared")
    )
    println("SUCCESS!")
}

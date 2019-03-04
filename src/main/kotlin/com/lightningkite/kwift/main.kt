package com.lightningkite.kwift

import java.io.File


fun main(vararg args: String) {
    println("Groan...")
    kwiftTask(
        listOf(
            File("/Users/josephivie/StudioProjects/penny-profit-android/app/src/main/java/com/lightningkite/pennyprofit/shared")
                    to
                    File("/Users/josephivie/Documents/GitHub/penny-profit-ios/PennyProfit/shared")
        )
    )
    println("SUCCESS!")
}

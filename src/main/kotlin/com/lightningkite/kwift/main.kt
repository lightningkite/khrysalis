package com.lightningkite.kwift

import java.io.File


fun main(vararg args: String) {
    println("Groan...")
    kwiftTask(
            directory = File("/Users/josephivie/IdeaProjects/kwift/testData"),
            outputDirectory = File("/Users/josephivie/IdeaProjects/kwift/testDataOutput")
    )
    println("SUCCESS!")
}

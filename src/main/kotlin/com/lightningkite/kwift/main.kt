package com.lightningkite.kwift

import com.lightningkite.kwift.layoutxml.xmlTask
import com.lightningkite.kwift.swift.ignoreKotlinOnly
import com.lightningkite.kwift.swift.kwiftTask
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import java.io.File


const val INTERFACE_SCAN_VERSION: Int = 2
const val VERSION: Int = 10

fun main(vararg args: String) {
//    testKwift()
    testXml()
//    testReader()
}

private fun testReader(){
    val text = """
        inLambda({
            var menteeSession = session as MenteeSession
        })
    """.trimIndent()
    val lexer = KotlinLexer(ANTLRInputStream(text))
    val tokenStream = CommonTokenStream(lexer)
    tokenStream.fill()
    println("Tokens: ")
    tokenStream.tokens.forEach {
        if(it.type == -1) return@forEach
        val typeName = KotlinLexer.VOCABULARY.getSymbolicName(it.type)
        println("${typeName}: ${it.text}")
    }
}

private fun testXml(){
    val resources = File("/Users/josephivie/StudioProjects/lifting-generations-android/app/src/main/res")
    val localbase = File("/Users/josephivie/StudioProjects/lifting-generations-android/app/../../lifting-generations-ios/Lifting Generations")
    val outputDir = File("/Users/josephivie/StudioProjects/lifting-generations-android/app/../../lifting-generations-ios/Lifting Generations/ios/xml")
    outputDir.mkdirs()
    xmlTask(resources, localbase, outputDir)
}

private fun testKwift() {
    println("Groan...")
    kwiftTask(
        listOf(
//            File("/Users/josephivie/StudioProjects/lifting-generations-android/app/src/main/java/org/liftinggenerations/shared") to
//                    File("/Users/josephivie/StudioProjects/lifting-generations-android/app/../../lifting-generations-ios/Lifting Generations/shared"),
//
//            File("/Users/josephivie/StudioProjects/lifting-generations-android/app/src/main/java/com/lightningkite/kwift/shared") to
//                    File("/Users/josephivie/StudioProjects/lifting-generations-android/app/../../lifting-generations-ios/kwift/shared"),

            File("/Users/josephivie/IdeaProjects/kwift/testData") to
                    File("/Users/josephivie/IdeaProjects/kwift/testDataOutput")

        )
//        listOf(
//            File("/Users/josephivie/IdeaProjects/kwift/testData")
//                    to
//                    File("/Users/josephivie/IdeaProjects/kwift/testDataOutput")
//        )
    )
    println("SUCCESS!")
}

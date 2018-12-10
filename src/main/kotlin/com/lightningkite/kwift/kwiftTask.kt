package com.lightningkite.kwift

import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener
import java.io.File
import java.util.jar.JarFile


fun kwiftTask(directory: File, outputDirectory: File) {
    outputDirectory.mkdirs()
    val interfaces = ArrayList<InterfaceListener.InterfaceData>()

    directory.walkTopDown()
        .filter { it.extension == "kt" }
        .forEach { file ->
            println("File: $file")
            val lexer = KotlinLexer(ANTLRFileStream(file.toString()))
            val tokenStream = CommonTokenStream(lexer)
            val parser = KotlinParser(tokenStream)

            val listener = InterfaceListener(parser)
            ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())

            interfaces += listener.interfaces
        }

    println("Interfaces: ${interfaces.joinToString("\n")}")

    directory.walkTopDown()
        .filter { it.extension == "kt" }
        .forEach { file ->
            println("File: $file")
            val lexer = KotlinLexer(ANTLRFileStream(file.toString()))
            val tokenStream = CommonTokenStream(lexer)
            val parser = KotlinParser(tokenStream)

            val listener = SwiftListener(tokenStream, parser, interfaces)
            ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())

            val output = File(
                outputDirectory.resolve(file.relativeTo(directory))
                    .toString()
                    .removeSuffix("kt")
                    .plus("swift")
            )
            output.writeText(listener.layers.last().last().toOutputString())
        }

}

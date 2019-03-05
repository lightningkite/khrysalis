package com.lightningkite.kwift

import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener
import java.io.File
import java.util.jar.JarFile


fun kwiftTask(pairs: List<Pair<File, File>>) {
    for ((inputDirectory, outputDirectory) in pairs) {
        println("Input directory $inputDirectory exists: ${inputDirectory.exists()}")
        if (outputDirectory.exists()) {
            outputDirectory.deleteRecursively()
        }
        outputDirectory.mkdirs()
    }
    val interfaces = ArrayList<InterfaceListener.InterfaceData>()

    pairs.forEach {
        it.first.walkTopDown()
            .filter { it.extension == "kt" }
            .forEach { file ->
                println("File: $file")
                val lexer = KotlinLexer(ANTLRInputStream(file.readText().ignoreKotlinOnly()))
                val tokenStream = CommonTokenStream(lexer)
                val parser = KotlinParser(tokenStream)

                val listener = InterfaceListener(parser)
                ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())

                interfaces += listener.interfaces
            }
    }

    println("Interfaces: ${interfaces.joinToString("\n")}")

    pairs.forEach { (directory, outputDirectory) ->
        directory.walkTopDown()
            .filter { it.extension == "kt" }
            .forEach { file ->
                println("File: $file")
                val lexer = KotlinLexer(ANTLRInputStream(file.readText().ignoreKotlinOnly()))
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
                output.parentFile.mkdirs()
                output.writeText("import Foundation\n" + listener.layers.last().last().toOutputString().retabSwift())
            }

    }
}

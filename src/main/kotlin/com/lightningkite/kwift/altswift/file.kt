package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerFile(){
    handle<KotlinParser.KotlinFileContext> { item ->
        line("//Package: ${item.packageHeader()?.identifier()?.text}")
        line("//Converted using Kwift2")
        line("")
        line("import Foundation")
        line("")
        line("")
        for (obj in item.topLevelObject()){
            write(obj)
        }
    }

    handle<KotlinParser.TopLevelObjectContext> { item ->
        item.classDeclaration()?.let{ write(it) }
        item.functionDeclaration()?.let{ write(it) }
        item.objectDeclaration()?.let{ write(it) }
        item.propertyDeclaration()?.let{ write(it) }
    }
}

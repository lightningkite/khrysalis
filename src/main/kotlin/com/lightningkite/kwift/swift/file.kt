package com.lightningkite.kwift.swift

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
}

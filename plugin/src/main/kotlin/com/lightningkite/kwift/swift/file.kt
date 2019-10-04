package com.lightningkite.kwift.swift

import com.lightningkite.kwift.swift.TabWriter
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerFile(){
    handle<KotlinParser.KotlinFileContext> { item ->
        line("//Package: ${item.packageHeader()?.identifier()?.text}")
        line("//Converted using Kwift2")
        line("")
        line("import Foundation")
        for(import in this@registerFile.imports){
            line("import $import")
        }
        line("")
        line("")
        for (obj in item.topLevelObject()){
            startLine()
            write(obj)
        }
    }
}

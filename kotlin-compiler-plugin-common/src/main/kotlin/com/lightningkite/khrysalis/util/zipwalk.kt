package com.lightningkite.khrysalis.util

import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.replacements.ReplacementRule
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

data class MaybeZipFile(
    val name: String,
    val isDirectory: Boolean,
    val inputStream: ()->InputStream
) {
    constructor(file: File):this(file.name, file.isDirectory, { file.inputStream() })
}

fun File.walkZip(): Sequence<MaybeZipFile> = walkTopDown()
    .flatMap {
        if(it.name.endsWith(".zip", true) || it.name.endsWith(".jar", true)) {
            sequenceOf(MaybeZipFile(it)) + ZipFile(it).walkZip()
        } else {
            sequenceOf(MaybeZipFile(it))
        }
    }

fun ZipFile.walkZip(): Sequence<MaybeZipFile> {
    return entries().asIterator().asSequence().map {
        MaybeZipFile(name = it.name, isDirectory = it.isDirectory, inputStream = { this.getInputStream(it) })
    }
}

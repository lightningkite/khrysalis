package com.lightningkite.khrysalis.utils

import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

data class MaybeZipFile(
    val name: String,
    val fileOrContainingZip: File,
    val pathInZip: String? = null,
    val isDirectory: Boolean,
    val inputStream: ()->InputStream
) {
    constructor(file: File):this(file.name, file, null, file.isDirectory, { file.inputStream() })

    override fun toString(): String {
        return fileOrContainingZip.toString() + (pathInZip?.let { "!/$it"} ?: "")
    }
}

fun File.walkZip(): Sequence<MaybeZipFile> = walkTopDown()
    .flatMap {
        if(it.name.endsWith(".zip", true) || it.name.endsWith(".jar", true)) {
            sequenceOf(MaybeZipFile(it)) + ZipFile(it).walkZip(it)
        } else {
            sequenceOf(MaybeZipFile(it))
        }
    }

fun ZipFile.walkZip(zipFile: File): Sequence<MaybeZipFile> {
    return entries().asIterator().asSequence().map {
        MaybeZipFile(name = it.name.substringAfterLast('/'), fileOrContainingZip = zipFile, pathInZip = it.name, isDirectory = it.isDirectory, inputStream = { this.getInputStream(it) })
    }
}

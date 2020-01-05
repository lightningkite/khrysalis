package com.lightningkite.kwift.flow

import com.lightningkite.kwift.swift.TabWriter
import java.io.StringWriter

data class CodeSection(
    val name: String,
    val overwrite: Boolean,
    val startLineSpaces: Int,
    val write: TabWriter.() -> Unit
) {
    companion object {
        const val overwriteMarker = "(overwritten on flow generation)"
        const val sectionMarker = "//---"

        fun read(lines: Iterable<String>): List<CodeSection> {
            val sections = ArrayList<CodeSection>()

            var currentSectionName: String = "!file"
            var currentSectionOverwrite: Boolean = false
            var currentStartLineSpaces: Int = 0
            var currentWrites = ArrayList<TabWriter.() -> Unit>()
            var currentSpaces = 0

            fun finishSection() {
                val writes = currentWrites
                sections += CodeSection(
                    name = currentSectionName,
                    overwrite = currentSectionOverwrite,
                    startLineSpaces = currentStartLineSpaces,
                    write = { writes.forEach { it.invoke(this) } }
                )
            }

            for (currentLine in lines) {
                val spaces = currentLine.asSequence().takeWhile { it == ' ' }.count()
                if (currentLine.trim().startsWith(sectionMarker)) {
                    //finish old section
                    finishSection()
                    //start new section
                    currentSectionName = currentLine.substringAfter(sectionMarker).substringBefore(overwriteMarker).trim()
                    currentSectionOverwrite = currentLine.contains(overwriteMarker)
                    currentStartLineSpaces = spaces
                    currentWrites = ArrayList<TabWriter.() -> Unit>()
                } else {
                    currentWrites.add {
                        this.spaces = spaces
                        line(currentLine.trim())
                    }
                }
                currentSpaces = spaces
            }
            finishSection()

            return sections
        }

        fun merge(old: String, new: String): String {
            val oldSections = read(old.lines())
            val newSections = read(new.lines())
            val stringWriter = StringBuilder()
            val tabWriter = TabWriter(stringWriter)
            oldSections.mergeOverride(newSections).forEach {
                it.writeWhole(tabWriter)
            }
            return stringWriter.toString()
        }
    }

    fun writeWhole(tabWriter: TabWriter) {
        if (name != "!file") {
            tabWriter.spaces = this.startLineSpaces
            tabWriter.line("$sectionMarker $name" + if (overwrite) " $overwriteMarker" else "")
        }
        write(tabWriter)
    }
}

fun TabWriter.section(key: String) = line("${CodeSection.sectionMarker} $key ${CodeSection.overwriteMarker}")
fun TabWriter.sectionPermanent(key: String) = line("${CodeSection.sectionMarker} $key")

fun List<CodeSection>.mergeOverride(other: List<CodeSection>): List<CodeSection> {
    val output = this.toMutableList()
    val otherMap = other.associate { it.name to it }
    for (index in output.indices) {
        val mine = output[index]
        if (mine.overwrite) {
            val theirs = otherMap[output[index].name]
            if (theirs != null) {
                output[index] = theirs
            }
        }
    }
    val myKeys = this.map { it.name }.toSet()
    other.forEachIndexed { index, added ->
        //Filter out any item that is in both
        if(added.name in myKeys) return@forEachIndexed

        //Find before item in other
        if(index > 0) {
            val previous = other[index - 1]
            val destinationIndex = output.indexOfFirst { it.name == previous.name } + 1
            //Ignore if it couldn't find the previous; should almost never happen
            if(destinationIndex != 0){
                //Insert the item just after
                output.add(destinationIndex, added)
            }
        }
    }
    output.removeAll { it.overwrite && it.name !in otherMap.keys }
    return output
}

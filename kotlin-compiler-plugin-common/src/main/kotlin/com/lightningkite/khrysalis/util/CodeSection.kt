package com.lightningkite.khrysalis.util

import java.io.StringWriter
import kotlin.text.Appendable

data class CodeSection(
    val name: String,
    val overwrite: Boolean,
    val startLineSpaces: Int,
    val write: Appendable.() -> Unit
) {
    companion object {
        const val overwriteMarker = "(overwritten on flow generation)"
        const val sectionMarker = "//---"

        fun read(lines: Iterable<String>): List<CodeSection> {
            val sections = ArrayList<CodeSection>()

            var currentSectionName: String = "!file"
            var currentSectionOverwrite: Boolean = false
            var currentStartLineSpaces: Int = 0
            var currentWrites = ArrayList<Appendable.() -> Unit>()

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
                    currentWrites = ArrayList<Appendable.() -> Unit>()
                } else {
                    currentWrites.add {
                        appendln(currentLine.trim())
                    }
                }
            }
            finishSection()

            return sections
        }

        fun merge(old: String, new: String): String {
            val oldSections = read(old.lines())
            val newSections = read(new.lines())
            val stringWriter = StringBuilder()
            val tabWriter = SmartTabWriter(stringWriter)
            oldSections.mergeOverride(newSections).forEach {
                it.writeWhole(tabWriter)
            }
            return stringWriter.toString().trimEnd('\n').plus('\n')
        }
    }

    fun writeWhole(tabWriter: SmartTabWriter) {
        if (name != "!file") {
            tabWriter.appendln("$sectionMarker $name" + if (overwrite) " $overwriteMarker" else "")
        }
        write(tabWriter)
    }
}

fun Appendable.section(key: String) = appendln("${CodeSection.sectionMarker} $key ${CodeSection.overwriteMarker}")
fun Appendable.sectionPermanent(key: String) = appendln("${CodeSection.sectionMarker} $key")

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

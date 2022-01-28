package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.formatting.retab
import com.lightningkite.khrysalis.replacements.Import
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

abstract class FileEmitter(val file: KtFile, val body: StringBuilder = StringBuilder()): Appendable by body {
    companion object {
        val overwriteWarning = "// Generated by Khrysalis - this file will be overwritten."
        val overwriteWarnings = setOf(
            overwriteWarning,
            "// Generated by Khrysalis Swift converter - this file will be overwritten.",
            "// Generated by Khrysalis TypeScript converter - this file will be overwritten."
        )
        fun canBeOverwritten(file: File): Boolean {
            val firstLines = file.useLines { it.take(3).toList() }
            return firstLines.any { it.trim() in overwriteWarnings }
        }
        fun canBeOverwritten(text: String): Boolean {
            return overwriteWarnings.any { text.contains(it) }
        }
    }
    data class StringReplacement(val from: String, val to: String)
    abstract fun addImport(import: Import): List<StringReplacement>
    var fileEndingActions = ArrayList<() -> Unit>()
    abstract fun renderImports(to: Appendable)
    abstract fun sub(): FileEmitter
    fun render(out: Appendable) {
        out.appendLine("// Package: ${file.packageFqName.asString()}")
        out.appendLine(overwriteWarning)
        renderImports(out)
        out.appendLine()
        out.append(body.toString().retab())
        while (fileEndingActions.isNotEmpty()) {
            val copy = fileEndingActions
            fileEndingActions = ArrayList()
            copy.forEach { it() }
        }
    }

    override fun toString(): String = buildString { render(this) }
}
package com.lightningkite.khrysalis

import org.jetbrains.kotlin.psi.KtFile
import java.io.File

fun determineTranslatable(file: KtFile): Boolean {
    if(file.packageFqName.asString().contains("shared")) return true
    if(file.virtualFilePath.contains(".shared"))
    if(file.annotationEntries.any { it.shortName?.asString()?.endsWith("SharedCode") == true }) return true
    if(file.text.contains("//! This file will translate using Khrysalis.")) return true
    return false
}
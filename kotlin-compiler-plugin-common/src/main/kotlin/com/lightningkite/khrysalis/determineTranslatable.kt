package com.lightningkite.khrysalis

import org.jetbrains.kotlin.psi.KtFile
import java.io.File

fun KtFile.shouldBeTranslated(): Boolean {
    if(this.packageFqName.asString().contains("shared")) return true
    if(this.virtualFilePath.contains(".shared")) return true
    if(this.annotationEntries.any { it.shortName?.asString()?.endsWith("SharedCode") == true }) return true
    if(this.text.contains("//! This file will translate using Khrysalis.")) return true
    return false
}
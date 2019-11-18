package com.lightningkite.kwift.swift

import org.jetbrains.kotlin.KotlinParser

fun KotlinParser.ModifiersContext?.visibilityString(): String {
    if(this?.modifier()?.asSequence()?.mapNotNull { it.inheritanceModifier() }?.any { it.OPEN() != null } == true)
        return "open"
    if(this?.modifier()?.asSequence()?.mapNotNull { it.inheritanceModifier() }?.any { it.ABSTRACT() != null } == true)
        return "open"
    val v = this?.modifier()?.asSequence()?.mapNotNull { it.visibilityModifier() }?.firstOrNull()
    return when {
        v == null -> "public"
        v.INTERNAL() != null -> ""
        v.PRIVATE() != null -> "private"
        v.PROTECTED() != null -> "protected"
        v.PUBLIC() != null -> "public"
        else -> "public"
    }
}

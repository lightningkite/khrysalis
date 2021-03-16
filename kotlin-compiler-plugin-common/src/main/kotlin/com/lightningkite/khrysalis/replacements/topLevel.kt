package com.lightningkite.khrysalis.replacements

import org.jetbrains.kotlin.types.KotlinType

lateinit var replacements: Replacements

fun KotlinType.requiresMutable(): Boolean {
    return replacements.requiresMutable(this)
}
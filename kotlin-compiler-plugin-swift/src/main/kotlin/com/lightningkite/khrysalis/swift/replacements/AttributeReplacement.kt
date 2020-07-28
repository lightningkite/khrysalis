package com.lightningkite.khrysalis.swift.replacements

data class AttributeReplacement(
    val id: String,
    val on: String? = null,
    val isColor: Boolean = false,
    val template: Template
): ReplacementRule {
    override val priority: Int
        get() = if (on != null) 2 else 0
}
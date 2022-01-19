package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.kotlin.types.KotlinType

data class CastRule(
    val from: String,
    val to: String,
    val template: Template,
    override val debug: Boolean
) : ReplacementRule {
    @get:JsonIgnore() override val priority: Int
        get() = 0

    fun passes(from: KotlinType, to: KotlinType): Boolean {
        return true
    }
}
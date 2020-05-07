package com.lightningkite.khrysalis.typescript.replacements

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType

data class TypeRefReplacement(
    val id: String,
    val template: Template
) : ReplacementRule {
    fun passes(decl: KotlinType): Boolean = true
    fun passes(decl: ClassDescriptor): Boolean = true
}
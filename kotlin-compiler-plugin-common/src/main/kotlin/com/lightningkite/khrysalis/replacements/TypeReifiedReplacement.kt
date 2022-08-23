package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType

data class TypeReifiedReplacement(
    val id: String,
    val template: Template,
    override val debug: Boolean = false,
) : ReplacementRule {
    fun passes(decl: KotlinType): Boolean = true
    fun passes(decl: DeclarationDescriptor): Boolean = true
}
package com.lightningkite.khrysalis.swift.replacements

import com.lightningkite.khrysalis.util.recursiveChildren
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType

data class TypeReplacement(
    val id: String,
    val typeArgumentRequirements: Map<Int, String>? = null,
    val requiresMutable: Boolean = false,
    val template: Template,
    val constraintTemplate: Template? = null,
    val xmlDefer: List<String> = listOf(),
    val xmlInit: String? = null,
    val xmlCreate: String? = null,
    val xmlAddChild: Template? = null,
    val xmlSkip: Boolean = false
) : ReplacementRule {
    override val priority: Int
        get() = typeArgumentRequirements?.size ?: 0

    fun passes(decl: KotlinType): Boolean {
        if (this.typeArgumentRequirements != null) {
            for ((key, value) in this.typeArgumentRequirements) {
                if (!decl.satisfies(value)) return false
            }
        }
        return true
    }

    fun passes(decl: DeclarationDescriptor): Boolean = typeArgumentRequirements == null || typeArgumentRequirements.isEmpty()
}
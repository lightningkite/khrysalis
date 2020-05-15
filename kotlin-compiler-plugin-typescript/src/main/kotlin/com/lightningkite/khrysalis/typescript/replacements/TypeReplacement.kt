package com.lightningkite.khrysalis.typescript.replacements

import com.lightningkite.khrysalis.util.recursiveChildren
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType

data class TypeReplacement(
    val id: String,
    val typeArgumentsHaveCustomEquals: List<Int> = listOf(),
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = typeArgumentsHaveCustomEquals.size

    fun passes(decl: KotlinType): Boolean = typeArgumentsHaveCustomEquals.all {
        (decl.arguments[it].type.constructor.declarationDescriptor as? ClassDescriptor)?.let {
            it.isData || it.recursiveChildren(DescriptorKindFilter.FUNCTIONS).any {
                it.name.asString() == "hashCode"
            }
        } == true
    }

    fun passes(decl: DeclarationDescriptor): Boolean = typeArgumentsHaveCustomEquals.isEmpty()
}
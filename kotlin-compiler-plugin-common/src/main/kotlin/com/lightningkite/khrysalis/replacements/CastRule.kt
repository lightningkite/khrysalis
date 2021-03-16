package com.lightningkite.khrysalis.replacements

import com.lightningkite.khrysalis.util.recursiveChildren
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType

data class CastRule(
    val consume: String,
    val given: String,
    val template: Template,
    override val debug: Boolean
) : ReplacementRule {
    override val priority: Int
        get() = 0

    fun passes(from: KotlinType, to: KotlinType): Boolean {
        return true
    }
}
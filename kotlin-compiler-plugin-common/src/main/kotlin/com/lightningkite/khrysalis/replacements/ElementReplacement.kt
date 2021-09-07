package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonProperty
import com.lightningkite.khrysalis.util.recursiveChildren
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType

data class ElementReplacement(
    val id: String,
    var template: Template,
    val insertChildrenAt: String? = null
) : ReplacementRule {

    override fun merge(other: ReplacementRule): Boolean {
        if(other !is ElementReplacement) return false
        if(this.id != other.id) return false
        this.template = other.template
        return true
    }

    override val priority: Int get() = 0

}
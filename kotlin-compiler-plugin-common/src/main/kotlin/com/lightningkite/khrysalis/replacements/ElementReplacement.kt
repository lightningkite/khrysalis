package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonIgnore
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
    var attributes: Map<String, String> = mapOf(),
    var deferTo: String? = null,
    var template: Template = Template(parts = listOf()),
    var childRule: String = "",
    var autoWrapFor: List<String> = listOf(),
    var insertChildrenAt: String? = null,
) : ReplacementRule {

    override fun merge(other: ReplacementRule): Boolean {
        if(other !is ElementReplacement) return false
        if(this.id != other.id) return false
        if(this.attributes != other.attributes) return false
        this.deferTo = other.deferTo
        this.template = other.template
        this.childRule = other.childRule
        this.insertChildrenAt = other.insertChildrenAt
        this.autoWrapFor = other.autoWrapFor
        return true
    }

    @get:JsonIgnore() override val priority: Int get() = attributes.size

}
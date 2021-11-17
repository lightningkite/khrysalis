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

data class TypeReplacement(
    val id: String,
    val typeArgumentRequirements: Map<Int, String>? = null,
    val requiresMutable: Boolean = false,
    override val debug: Boolean = false,
    val template: Template,
    @JsonProperty("protocol") val protocol: Boolean = false,
    var isFunctionType: Boolean = false,
    var typeArgumentNames: List<String>? = null,
    var errorCondition: Template? = null,
    var constraintTemplate: Template? = null
) : ReplacementRule {

    override fun merge(other: ReplacementRule): Boolean {
        if(other !is TypeReplacement) return false
        if(
            this.id != other.id ||
            this.typeArgumentRequirements != other.typeArgumentRequirements
        ) return false
        this.typeArgumentNames = other.typeArgumentNames
        this.errorCondition = other.errorCondition
        this.constraintTemplate = other.constraintTemplate
        return true
    }

    @get:JsonIgnore() override val priority: Int
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
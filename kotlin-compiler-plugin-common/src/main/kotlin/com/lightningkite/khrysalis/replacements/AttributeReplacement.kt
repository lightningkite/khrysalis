package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonProperty
import com.lightningkite.khrysalis.util.recursiveChildren
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType
import org.w3c.dom.Node
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

data class AttributeReplacement(
    val id: String,
    var valueType: ValueType = ValueType.String,
    var typeRequirement: String? = null,
    var at: String,
    var append: List<Template> = listOf(),
    var set: Map<String, Template> = mapOf(),
    var css: Map<String, Template> = mapOf()
) : ReplacementRule {

    enum class ValueType {
        Color,
        ColorResource,
        DrawableResource,
        Number,
        String,
        StringResource
    }

    override fun merge(other: ReplacementRule): Boolean {
        if(other !is AttributeReplacement) return false
        if(this.id != other.id) return false
        if(this.valueType != other.valueType) return false
        this.typeRequirement = other.typeRequirement
        this.at = other.at
        this.append = other.append
        this.set = other.set
        this.css = other.css
        return true
    }

    override val priority: Int
        get() = (if(typeRequirement != null) 1 else 0)

}

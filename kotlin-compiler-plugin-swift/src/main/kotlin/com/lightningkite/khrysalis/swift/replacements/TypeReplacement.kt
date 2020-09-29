package com.lightningkite.khrysalis.swift.replacements

import com.lightningkite.khrysalis.swift.replacements.xib.XibConstraintTemplate
import com.lightningkite.khrysalis.swift.replacements.xib.XibNodeTemplate
import com.lightningkite.khrysalis.swift.replacements.xib.XibUDNodeTemplate
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
    val typeArgumentNames: List<String>? = null,
    val errorCondition: Template? = null,
    val constraintTemplate: Template? = null,
    val constraintTemplates: List<Template>? = null,

    val xibName: String? = null,
    val xibCustomView: String? = null,
    val xibCustomModule: String? = null,
    val iosParent: String? = "UIView",
    val xibCode: Template? = null,
    val xibProperties: Map<String, XibNodeTemplate>? = null,
    val xibCustomProperties: Map<String, XibUDNodeTemplate>? = null,
    val xibAttributes: Map<String, Template>? = null,
    val xibConstraints: List<XibConstraintTemplate>? = null
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
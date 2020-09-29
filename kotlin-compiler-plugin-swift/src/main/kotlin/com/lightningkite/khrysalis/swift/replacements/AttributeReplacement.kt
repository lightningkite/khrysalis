package com.lightningkite.khrysalis.swift.replacements

import com.lightningkite.khrysalis.swift.replacements.xib.XibConstraintTemplate
import com.lightningkite.khrysalis.swift.replacements.xib.XibNodeTemplate
import com.lightningkite.khrysalis.swift.replacements.xib.XibResourceTemplate
import com.lightningkite.khrysalis.swift.replacements.xib.XibUDNodeTemplate

data class AttributeReplacement(
    val id: String,
    val on: String? = null,
    val valueType: AndroidXmlValueType? = null,
    val value: String? = null,
    val valueContains: String? = null,
    val filters: List<TemplateCondition> = listOf(),
    val isImage: Boolean? = null,
    val isSet: Boolean? = null,

    val codeTemplate: Template? = null,
    val xibProperties: Map<String, XibNodeTemplate>? = null,
    val xibCustomProperties: Map<String, XibUDNodeTemplate>? = null,
    val xibAttributes: Map<String, Template>? = null,
    val xibConstraints: List<XibConstraintTemplate>? = null,
    val xibResources: List<XibResourceTemplate>? = null
): ReplacementRule {

    enum class AndroidXmlValueType {
        DRAWABLE, COLOR, NUMBER, DIMENSION, BOOLEAN, ENUM_OR_STRING, UNKNOWN
    }
    override val priority: Int
        get() = if (on != null) 2 else 0
}

data class TemplateCondition(
    val template: Template = Template(listOf()),
    val value: String = ""
) {
    fun satisfied(resolver: (Template) -> String): Boolean {
        return resolver(template) == value
    }
}
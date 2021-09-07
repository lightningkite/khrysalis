package com.lightningkite.khrysalis.replacements

data class AttributeReplacement(
    val id: String,
    var valueType: ValueType = ValueType.String,
    var element: String? = null,
    var rules: Map<String, SubRule> = mapOf(),
) : ReplacementRule {

    data class SubRule(
        val append: List<Template> = listOf(),
        val attribute: Map<String, Template> = mapOf(),
        var css: Map<String, Template> = mapOf()
    )

    enum class ValueType {
        Font,
        Color,
        ColorResource,
        ColorStateResource,
        DrawableResource,
        LayoutResource,
        Dimension,
        DimensionResource,
        Number,
        String,
        StringResource,
        Style,
    }

    override fun merge(other: ReplacementRule): Boolean {
        if(other !is AttributeReplacement) return false
        if(this.id != other.id) return false
        if(this.valueType != other.valueType) return false
        this.element = other.element
        this.rules = other.rules
        return true
    }

    override val priority: Int
        get() = (if(element != null) 1 else 0)

}

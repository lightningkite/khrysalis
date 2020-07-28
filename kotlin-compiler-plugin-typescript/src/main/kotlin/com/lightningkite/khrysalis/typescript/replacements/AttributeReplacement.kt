package com.lightningkite.khrysalis.typescript.replacements

data class AttributeReplacement(
    val id: String,
    val on: String? = null,
    val effects: List<Effect> = listOf()
): ReplacementRule {

    data class Effect(
        val applyTo: ApplyTo = ApplyTo.Main,
        val css: Map<String, Template> = mapOf(),
        val attributes: Map<String, Template> = mapOf()
    )

    enum class ApplyTo {
        Main, Primary, Text, ContainerNode
    }

    override val priority: Int
        get() = if (on != null) 2 else 0
}
package com.lightningkite.kwift.layout

data class LayoutConverter(
    val imports: Set<String> = setOf("UIKit", "Kwift"),
    val viewTypes: Map<String, ViewType> = mapOf(),
    val skipTypes: List<String> = listOf()
) {
    constructor(vararg others: LayoutConverter):this(
        imports = others.fold(setOf()) { current, other -> current + other.imports },
        viewTypes = others.fold(mapOf()) { current, other -> current + other.viewTypes },
        skipTypes = others.fold(listOf()) { current, other -> current + other.skipTypes }
    )
    operator fun plus(other: LayoutConverter): LayoutConverter = LayoutConverter(
        imports = this.imports + other.imports,
        viewTypes = this.viewTypes + other.viewTypes,
        skipTypes = this.skipTypes + other.skipTypes
    )
    companion object
}

package com.lightningkite.kwift.layout

data class LayoutConverter(
    val viewTypes: Map<String, ViewType> = mapOf(),
    val skipTypes: List<String> = listOf()
) {
    constructor(vararg others: LayoutConverter):this(
        viewTypes = others.fold(mapOf()) { current, other -> current + other.viewTypes },
        skipTypes = others.fold(listOf()) { current, other -> current + other.skipTypes }
    )
    operator fun plus(other: LayoutConverter): LayoutConverter = LayoutConverter(
        viewTypes = this.viewTypes + other.viewTypes,
        skipTypes = this.skipTypes + other.skipTypes
    )
    companion object
}

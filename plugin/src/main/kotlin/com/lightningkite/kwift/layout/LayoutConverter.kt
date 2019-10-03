package com.lightningkite.kwift.layout

data class LayoutConverter(
    val viewTypes: Map<String, ViewType>,
    val skipTypes: List<String>
) {
    companion object
}

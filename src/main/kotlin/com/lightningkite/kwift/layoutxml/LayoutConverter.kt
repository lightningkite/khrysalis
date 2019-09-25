package com.lightningkite.kwift.layoutxml

data class LayoutConverter(
    val viewTypes: Map<String, ViewType>,
    val skipTypes: List<String>
) {
    companion object
}

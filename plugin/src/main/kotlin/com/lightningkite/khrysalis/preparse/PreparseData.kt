package com.lightningkite.khrysalis.preparse

data class PreparseData(
    val interfaces: Map<String, InterfaceData> = mapOf(),
    val declarations: Map<String, String> = mapOf()
)

package com.lightningkite.khrysalis.preparse

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class InterfaceData(
    @JsonProperty("packageName") val packageName: String = "",
    val name: String = "",
    val methods: List<String> = listOf(),
    val properties: List<String> = listOf(),
    val implements: List<String> = listOf()
) {
    @get:JsonIgnore
    val qualifiedName
        get() = packageName + "." + name

    override fun toString(): String {
        return "$name: ${implements.joinToString()} { ${methods.joinToString(" ") { it + "()" }} ${properties.joinToString(
            " "
        )} }"
    }
}

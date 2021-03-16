package com.lightningkite.khrysalis.swift.replacements

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.lightningkite.khrysalis.replacements.Import
import com.lightningkite.khrysalis.replacements.JacksonReplacementsModule

class SwiftJacksonReplacementsModule() : JacksonReplacementsModule() {

    override fun parseImports(node: JsonNode): List<Import> {
        return (node as ArrayNode).map { SwiftImport(it.asText()) }
    }
}
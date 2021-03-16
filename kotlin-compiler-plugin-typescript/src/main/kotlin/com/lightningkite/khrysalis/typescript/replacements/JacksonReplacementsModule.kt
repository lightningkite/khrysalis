package com.lightningkite.khrysalis.typescript.replacements

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lightningkite.khrysalis.replacements.Import
import com.lightningkite.khrysalis.replacements.JacksonReplacementsModule
import com.lightningkite.khrysalis.replacements.Template

class TypescriptJacksonReplacementsModule() : JacksonReplacementsModule() {
    override fun parseImports(node: JsonNode): List<Import> {
        return (node as? ObjectNode)?.fields()?.asSequence()?.map { (key, value) ->
            val valueText = value.asText()
            when {
                valueText.startsWith("DIRECT from ") -> TypescriptImport(
                    path = valueText.substringAfter(" from "),
                    identifier = TypescriptImport.WHOLE,
                    asName = key
                )
                valueText.contains(" from ") -> TypescriptImport(
                    path = valueText.substringAfter(" from "),
                    identifier = valueText.substringBefore(" from "),
                    asName = key
                )
                valueText.contains(" as ") -> TypescriptImport(
                    path = valueText.substringBefore(" as "),
                    identifier = key,
                    asName = valueText.substringAfter(" as ")
                )
                key.contains(" as ") -> TypescriptImport(
                    path = valueText,
                    identifier = key.substringBefore(" as "),
                    asName = key.substringAfter(" as ", "")
                )
                else -> TypescriptImport(
                    path = valueText,
                    identifier = key
                )
            }
        }?.toList() ?: listOf()
    }
}
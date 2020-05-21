package com.lightningkite.khrysalis.typescript.replacements

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

class JacksonReplacementsModule() : SimpleModule() {
    init {
        addDeserializer(Template::class.java, object : StdDeserializer<Template>(
            Template::class.java
        ) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Template {
                when (p.currentToken) {
                    JsonToken.START_OBJECT -> {
                        val obj = p.readValueAsTree<ObjectNode>()
                        return Template.fromString(
                            obj["pattern"].asText(),
                            (obj["imports"] as? ObjectNode)?.fields()?.asSequence()?.map { (key, value) ->
                                val valueText = value.asText()
                                when {
                                    valueText.startsWith("DIRECT from ") -> TemplatePart.Import(
                                        path = valueText.substringAfter(" from "),
                                        identifier = TemplatePart.Import.WHOLE,
                                        asName = key
                                    )
                                    valueText.contains(" from ") -> TemplatePart.Import(
                                        path = valueText.substringAfter(" from "),
                                        identifier = valueText.substringBefore(" from "),
                                        asName = key
                                    )
                                    valueText.contains(" as ") -> TemplatePart.Import(
                                        path = valueText.substringBefore(" as "),
                                        identifier = key,
                                        asName = valueText.substringAfter(" as ")
                                    )
                                    key.contains(" as ") -> TemplatePart.Import(
                                        path = valueText,
                                        identifier = key.substringBefore(" as "),
                                        asName = key.substringAfter(" as ", "")
                                    )
                                    else -> TemplatePart.Import(
                                        path = valueText,
                                        identifier = key
                                    )
                                }
                            }?.toList() ?: listOf(),
                            (obj["lambdaTransforms"] as? ObjectNode)?.fields()?.asSequence()?.associate { (key, value) ->
                                key.toInt() to (value as ArrayNode).map { Template.fromString(it.asText()).parts }
                            } ?: mapOf()
                        )
                    }
                    JsonToken.VALUE_STRING -> {
                        val text = p.text
                        return Template.fromString(text)
                    }
                    else -> throw IllegalArgumentException("A template must be a string or object!")
                }
            }
        })
    }
}
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
                        val text = p.readValueAsTree<ObjectNode>()
                        return Template.fromString(
                            text["pattern"].asText(),
                            (text["imports"] as ObjectNode).fields().asSequence().map { (key, value) ->
                                TemplatePart.Import(
                                    path = value.asText(),
                                    identifier = key.substringBefore(" as "),
                                    asName = key.substringAfter(" as ", "").takeUnless { it.isEmpty() }
                                )
                            }.toList()
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
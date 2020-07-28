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

                        val imports = (obj["imports"] as? ObjectNode)?.fields()?.asSequence()?.map { (key, value) ->
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
                        }?.toList() ?: listOf()

                        val transforms = (obj["lambdaTransforms"] as? ObjectNode)?.fields()?.asSequence()?.associate { (key, value) ->
                            key.toInt() to (value as ArrayNode).map { Template.fromString(it.asText()) }
                        } ?: mapOf()


                        (obj["split"] as? ObjectNode)?.let {
                            return Template(parts = listOf(TemplatePart.Split(
                                on = Template.partFromString(it["on"].textValue()),
                                name = it["name"]?.textValue() ?: "i",
                                by = it["by"].textValue(),
                                before = Template.partFromString(it["before"].textValue()),
                                between = Template.partFromString(it["between"].textValue()),
                                after = Template.partFromString(it["after"].textValue()),
                                each = it["each"].traverse().readValueAs(Template::class.java)
                            )) + imports)
                        }
                        (obj["switch"] as? ObjectNode)?.let {
                            val cases = it["cases"] as ObjectNode
                            return Template(parts = listOf(TemplatePart.Switch(
                                on = Template.partFromString(it["on"].textValue()),
                                name = it["name"]?.textValue() ?: "i",
                                cases = cases.fields().asSequence().associate {
                                    it.key to it.value.traverse().readValueAs(Template::class.java)
                                }
                            )) + imports)
                        }
                        return Template.fromString(
                            obj["pattern"].asText(),
                            imports,
                            transforms
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
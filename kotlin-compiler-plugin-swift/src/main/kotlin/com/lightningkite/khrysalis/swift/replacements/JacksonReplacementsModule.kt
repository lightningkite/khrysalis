package com.lightningkite.khrysalis.swift.replacements

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lightningkite.khrysalis.swift.replacements.xib.AttKind
import com.lightningkite.khrysalis.swift.replacements.xib.AttPath
import com.lightningkite.khrysalis.swift.replacements.xib.XibTypedValue

class JacksonReplacementsModule() : SimpleModule() {
    init {
        addDeserializer(Template::class.java, object : StdDeserializer<Template>(
            Template::class.java
        ) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Template {
                when (p.currentToken) {
                    JsonToken.START_OBJECT -> {
                        val obj = p.readValueAsTree<ObjectNode>()

                        val imports = (obj["imports"] as? ArrayNode)?.map { TemplatePart.Import(it.asText()) } ?: listOf()

                        val transforms = (obj["lambdaTransforms"] as? ObjectNode)?.fields()?.asSequence()?.associate { (key, value) ->
                            key.toInt() to (value as ArrayNode).map { Template.fromString(it.asText()) }
                        } ?: mapOf()

                        (obj["split"] as? ObjectNode)?.let {
                            return Template(parts = listOf(TemplatePart.Split(
                                on = Template.partFromString(it["on"].textValue()),
                                name = it["name"]?.textValue() ?: "i",
                                by = it["by"].textValue(),
                                before = it["before"]?.textValue()?.let { Template.partFromString(it) },
                                between = it["between"]?.textValue()?.let { Template.partFromString(it) },
                                after = it["after"]?.textValue()?.let { Template.partFromString(it) },
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
        addDeserializer(AttPath::class.java, object: StdDeserializer<AttPath>(AttPath::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AttPath {
                when(p.currentToken) {
                    JsonToken.VALUE_STRING -> {
                        val text = p.text
                        return AttPath(text)
                    }
                    else -> throw IllegalArgumentException("REEEEEEEE got ${p.currentToken}")
                }
            }
        })
        addDeserializer(XibTypedValue::class.java, object: StdDeserializer<XibTypedValue>(XibTypedValue::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): XibTypedValue {
                when(p.currentToken) {
                    JsonToken.START_OBJECT -> {
                        val obj = p.readValueAsTree<ObjectNode>()
                        return XibTypedValue(
                            value = obj["value"].asText(),
                            type = AttKind.parse(obj["type"].asText())
                        )
                    }
                    else -> {
                        return XibTypedValue(p.valueAsString)
                    }
                }
            }
        })
        addDeserializer(AttKind::class.java, object: StdDeserializer<AttKind>(AttKind::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AttKind {
                return AttKind.parse(p.valueAsString)
            }
        })
    }
}
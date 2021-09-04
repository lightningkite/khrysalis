package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

class XibRulesModule() : SimpleModule() {
    init {
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
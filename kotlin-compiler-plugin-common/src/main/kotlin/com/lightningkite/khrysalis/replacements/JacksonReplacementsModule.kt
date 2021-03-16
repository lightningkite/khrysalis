package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

abstract class JacksonReplacementsModule() : SimpleModule() {

    abstract fun parseImports(node: JsonNode): List<Import>

    init {
        addDeserializer(Template::class.java, object : StdDeserializer<Template>(
            Template::class.java
        ) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Template {
                when (p.currentToken) {
                    JsonToken.START_OBJECT -> {
                        val obj = p.readValueAsTree<ObjectNode>()

                        val imports = obj["imports"]?.let { parseImports(it) } ?: listOf()

                        val transforms = (obj["lambdaTransforms"] as? ObjectNode)?.fields()?.asSequence()?.associate { (key, value) ->
                            key.toInt() to (value as ArrayNode).map { Template.fromString(it.asText()) }
                        } ?: mapOf()

//                        (obj["split"] as? ObjectNode)?.let {
//                            return Template(
//                                parts = listOf(TemplatePart.Split(
//                                    on = Template.partFromString(it["on"].textValue()),
//                                    name = it["name"]?.textValue() ?: "i",
//                                    by = it["by"].textValue(),
//                                    before = it["before"]?.textValue()?.let { Template.partFromString(it) },
//                                    between = it["between"]?.textValue()?.let { Template.partFromString(it) },
//                                    after = it["after"]?.textValue()?.let { Template.partFromString(it) },
//                                    each = it["each"].traverse().readValueAs(Template::class.java)
//                                )), imports = listOf()
//                            )
//                        }
//                        (obj["switch"] as? ObjectNode)?.let {
//                            val cases = it["cases"] as ObjectNode
//                            return Template(parts = listOf(TemplatePart.Switch(
//                                on = Template.partFromString(it["on"].textValue()),
//                                name = it["name"]?.textValue() ?: "i",
//                                cases = cases.fields().asSequence().associate {
//                                    it.key to it.value.traverse().readValueAs(Template::class.java)
//                                }
//                            )), imports = listOf())
//                        }
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
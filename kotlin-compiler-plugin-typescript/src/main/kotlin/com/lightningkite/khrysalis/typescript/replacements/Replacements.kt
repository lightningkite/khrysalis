package com.lightningkite.khrysalis.typescript.replacements

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.util.isAtEnd
import com.lightningkite.khrysalis.util.readUntil
import com.lightningkite.khrysalis.util.skipWhitespace
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.inline.util.zipWithDefault
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import java.io.File
import java.io.PushbackReader
import java.lang.IllegalArgumentException

class Replacements() {
    val functions: HashMap<String, ArrayList<FunctionReplacement>> = HashMap()
    val gets: HashMap<String, ArrayList<GetReplacement>> = HashMap()
    val sets: HashMap<String, ArrayList<SetReplacement>> = HashMap()
    val types: HashMap<String, ArrayList<TypeReplacement>> = HashMap()
    val typeRefs: HashMap<String, ArrayList<TypeRefReplacement>> = HashMap()

    fun getCall(functionDescriptor: FunctionDescriptor): FunctionReplacement? =
        functions[functionDescriptor.fqNameSafe.asString()]?.find { it.passes(functionDescriptor) }

    fun getGet(propertyDescriptor: PropertyDescriptor): GetReplacement? =
        gets[propertyDescriptor.fqNameSafe.asString()]?.find { it.passes(propertyDescriptor) }

    fun getSet(propertyDescriptor: PropertyDescriptor): SetReplacement? =
        sets[propertyDescriptor.fqNameSafe.asString()]?.find { it.passes(propertyDescriptor) }

    fun getType(type: KotlinType): TypeReplacement? = types[type.getJetTypeFqName(false)]?.find { it.passes(type) }
    fun getTypeRef(type: KotlinType): TypeRefReplacement? =
        typeRefs[type.getJetTypeFqName(false)]?.find { it.passes(type) }

    companion object {
        val mapper: ObjectMapper = ObjectMapper(YAMLFactory())
            .registerModule(JacksonReplacementsModule())
            .registerModule(KotlinModule())
//            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    operator fun plusAssign(item: ReplacementRule) {
        when (item) {
            is FunctionReplacement -> functions.getOrPut(item.id) { ArrayList() }.add(item)
            is GetReplacement -> gets.getOrPut(item.id) { ArrayList() }.add(item)
            is SetReplacement -> sets.getOrPut(item.id) { ArrayList() }.add(item)
            is TypeReplacement -> types.getOrPut(item.id) { ArrayList() }.add(item)
            is TypeRefReplacement -> typeRefs.getOrPut(item.id) { ArrayList() }.add(item)
        }
    }

    operator fun plusAssign(yaml: String) {
        mapper.readValue<List<ReplacementRule>>(yaml).forEach {
            this += it
        }
    }

    operator fun plusAssign(yaml: File) {
        mapper.readValue<List<ReplacementRule>>(yaml).forEach {
            this += it
        }
    }
}

class JacksonReplacementsModule() : SimpleModule() {
    init {
        addDeserializer(Template::class.java, object : StdDeserializer<Template>(Template::class.java) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Template {
                val text = p.text
                return Template.fromString(text)
            }
        })
    }
}

data class Template(val parts: List<TemplatePart>) : Iterable<TemplatePart> {
    override fun iterator(): Iterator<TemplatePart> = parts.iterator()

    companion object {
        val tagRegex = Regex("""~([a-zA-Z0-9]+)~""")
        fun fromString(text: String): Template {
            val tags = tagRegex.findAll(text).map {
                val tag = it.groupValues[1]
                val firstChar = tag.firstOrNull() ?: return@map TemplatePart.Text("")
                when {
                    tag == "this" -> {
                        TemplatePart.Receiver
                    }
                    tag == "thisExtension" -> {
                        TemplatePart.ExtensionReceiver
                    }
                    tag == "thisDispatch" -> {
                        TemplatePart.DispatchReceiver
                    }
                    tag == "value" -> {
                        TemplatePart.Value
                    }
                    firstChar.isDigit() -> {
                        TemplatePart.ParameterByIndex(tag.toInt())
                    }
                    firstChar == 'T' && tag.getOrNull(1)?.isDigit() == true -> {
                        TemplatePart.TypeParameterByIndex(tag.drop(1).toInt())
                    }
                    firstChar.isUpperCase() -> {
                        TemplatePart.TypeParameter(tag)
                    }
                    else -> {
                        TemplatePart.Parameter(tag)
                    }
                }
            }.toList()
            val other = text.split(tagRegex).map { TemplatePart.Text(it) }
            return Template(
                other
                    .withIndex()
                    .flatMap { (index, it) ->
                        listOf(it, tags.getOrElse(index) { TemplatePart.Text("") })
                    }
                    .filter { it !is TemplatePart.Text || it.string.isNotEmpty() }
            )
        }
    }
}

sealed class TemplatePart {
    class Text(val string: String) : TemplatePart()
    object Receiver : TemplatePart()
    object DispatchReceiver : TemplatePart()
    object ExtensionReceiver : TemplatePart()
    object Value : TemplatePart()
    class Parameter(val name: String) : TemplatePart()
    class TypeParameter(val name: String) : TemplatePart()
    class ParameterByIndex(val index: Int) : TemplatePart()
    class TypeParameterByIndex(val index: Int) : TemplatePart()
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(FunctionReplacement::class, name = "call"),
    JsonSubTypes.Type(GetReplacement::class, name = "get"),
    JsonSubTypes.Type(SetReplacement::class, name = "set"),
    JsonSubTypes.Type(TypeReplacement::class, name = "type"),
    JsonSubTypes.Type(TypeRefReplacement::class, name = "typeRef")
)
interface ReplacementRule

data class FunctionReplacement(
    val id: String,
    val receiver: String? = null,
    val arguments: List<String>? = null,
    val template: Template
) : ReplacementRule {
    fun passes(decl: FunctionDescriptor): Boolean {
        return decl.fqNameSafe.asString() == id &&
                (receiver == null || receiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                )) && (arguments == null || decl.valueParameters.zip(arguments)
            .all { (p, a) -> p.type.getJetTypeFqName(false) == a })
    }
}

data class GetReplacement(
    val id: String,
    val receiver: String? = null,
    val template: Template
) : ReplacementRule {
    fun passes(decl: PropertyDescriptor): Boolean {
        return decl.fqNameSafe.asString() == id &&
                (receiver == null || receiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                ))
    }
}

data class SetReplacement(
    val id: String,
    val receiver: String? = null,
    val template: Template
) : ReplacementRule {
    fun passes(decl: PropertyDescriptor): Boolean {
        return decl.fqNameSafe.asString() == id &&
                (receiver == null || receiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                ))
    }
}

data class TypeReplacement(
    val id: String,
    val template: Template
) : ReplacementRule {
    fun passes(decl: KotlinType): Boolean {
        return decl.getJetTypeFqName(false) == id
    }
}

data class TypeRefReplacement(
    val id: String,
    val template: Template
) : ReplacementRule {
    fun passes(decl: KotlinType): Boolean {
        return decl.getJetTypeFqName(false) == id
    }
}
package com.lightningkite.khrysalis.typescript.replacements

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class Replacements() {
    val functions: HashMap<String, TreeSet<FunctionReplacement>> = HashMap()
    val gets: HashMap<String, TreeSet<GetReplacement>> = HashMap()
    val sets: HashMap<String, TreeSet<SetReplacement>> = HashMap()
    val types: HashMap<String, TreeSet<TypeReplacement>> = HashMap()
    val typeRefs: HashMap<String, TreeSet<TypeRefReplacement>> = HashMap()

    fun getCall(functionDescriptor: FunctionDescriptor, comparatorType: String? = null): FunctionReplacement? {
        return functions[functionDescriptor.fqNameSafe.asString().substringBefore(".<")]?.find { it.passes(functionDescriptor, comparatorType) }
            ?: functionDescriptor.overriddenDescriptors.asSequence().map { getCall(it) }.firstOrNull()
    }

    fun getGet(propertyDescriptor: PropertyDescriptor): GetReplacement? =
        gets[propertyDescriptor.fqNameSafe.asString()]?.find { it.passes(propertyDescriptor) }
            ?: propertyDescriptor.overriddenDescriptors.asSequence().map { getGet(it) }.firstOrNull()

    fun getSet(propertyDescriptor: PropertyDescriptor): SetReplacement? =
        sets[propertyDescriptor.fqNameSafe.asString()]?.find { it.passes(propertyDescriptor) }
            ?: propertyDescriptor.overriddenDescriptors.asSequence().map { getSet(it) }.firstOrNull()

    fun getType(type: DeclarationDescriptor): TypeReplacement? = types[type.fqNameSafe.asString()]?.find { it.passes(type) }
    fun getType(type: KotlinType): TypeReplacement? = types[type.getJetTypeFqName(false)]?.find { it.passes(type) }
    fun getTypeRef(type: KotlinType): TypeRefReplacement? = typeRefs[type.getJetTypeFqName(false)]?.find { it.passes(type) }
    fun getTypeRef(type: DeclarationDescriptor): TypeRefReplacement? = typeRefs[type.fqNameSafe.asString()]?.find { it.passes(type) }

    companion object {
        val mapper: ObjectMapper = ObjectMapper(YAMLFactory())
            .registerModule(JacksonReplacementsModule())
            .registerModule(KotlinModule())
//            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    operator fun plusAssign(item: ReplacementRule) {
        when (item) {
            is FunctionReplacement -> functions.getOrPut(item.id) { TreeSet() }.add(item)
            is GetReplacement -> gets.getOrPut(item.id) { TreeSet() }.add(item)
            is SetReplacement -> sets.getOrPut(item.id) { TreeSet() }.add(item)
            is TypeReplacement -> types.getOrPut(item.id) { TreeSet() }.add(item)
            is TypeRefReplacement -> typeRefs.getOrPut(item.id) { TreeSet() }.add(item)
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


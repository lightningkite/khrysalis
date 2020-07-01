package com.lightningkite.khrysalis.swift.replacements

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.util.*
import org.jetbrains.kotlin.codegen.AccessorForCompanionObjectInstanceFieldDescriptor
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Replacements() {
    val functions: HashMap<String, TreeSet<FunctionReplacement>> = HashMap()
    val gets: HashMap<String, TreeSet<GetReplacement>> = HashMap()
    val sets: HashMap<String, TreeSet<SetReplacement>> = HashMap()
    val types: HashMap<String, TreeSet<TypeReplacement>> = HashMap()
    val typeRefs: HashMap<String, TreeSet<TypeRefReplacement>> = HashMap()

    fun getCall(
        analysis: AnalysisExtensions,
        call: ResolvedCall<out CallableDescriptor>,
        descriptor: CallableDescriptor = call.candidateDescriptor,
        alreadyChecked: HashSet<CallableDescriptor> = HashSet()
    ): FunctionReplacement? {
        if(!alreadyChecked.add(descriptor)) return null
        val result =  functions[descriptor.simpleFqName.substringBefore(".<")]?.find {
            it.passes(
                analysis = analysis,
                call = call,
                descriptor = descriptor
            )
        }
            ?: functions[descriptor.simplerFqName.substringBefore(".<")]?.find {
                it.passes(
                    analysis = analysis,
                    call = call,
                    descriptor = descriptor
                )
            }
            ?: (descriptor as? CallableMemberDescriptor)?.allOverridden()
                ?.map {
                    getCall(
                        analysis = analysis,
                        call = call,
                        descriptor = it,
                        alreadyChecked = alreadyChecked
                    )
                }
                ?.firstOrNull()
        return result
    }

    fun getGet(propertyDescriptor: PropertyDescriptor, receiverType: KotlinType? = null): GetReplacement? =
        gets[propertyDescriptor.simpleFqName]?.find { it.passes(propertyDescriptor, receiverType) }
            ?: gets[propertyDescriptor.simplerFqName]?.find { it.passes(propertyDescriptor, receiverType) }
            ?: propertyDescriptor.overriddenDescriptors.asSequence().map { getGet(it, receiverType) }.firstOrNull()

    fun getGet(objectDescriptor: DeclarationDescriptor): GetReplacement? =
        gets[objectDescriptor.simpleFqName]?.firstOrNull()

    fun getSet(propertyDescriptor: PropertyDescriptor, receiverType: KotlinType? = null): SetReplacement? =
        sets[propertyDescriptor.simpleFqName]?.find { it.passes(propertyDescriptor, receiverType) }
            ?: sets[propertyDescriptor.simplerFqName]?.find { it.passes(propertyDescriptor, receiverType) }
            ?: propertyDescriptor.overriddenDescriptors.asSequence().map { getSet(it, receiverType) }.firstOrNull()

    fun getType(type: DeclarationDescriptor): TypeReplacement? =
        types[type.simpleFqName]?.find { it.passes(type) }
            ?: types[type.simplerFqName]?.find { it.passes(type) }

    fun getType(type: KotlinType): TypeReplacement? = types[type.getJetTypeFqName(false)]?.find { it.passes(type) }
    fun getTypeRef(type: KotlinType): TypeRefReplacement? =
        typeRefs[type.getJetTypeFqName(false)]?.find { it.passes(type) }

    fun getTypeRef(type: DeclarationDescriptor): TypeRefReplacement? =
        typeRefs[type.simpleFqName]?.find { it.passes(type) }
            ?: typeRefs[type.simplerFqName]?.find { it.passes(type) }

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


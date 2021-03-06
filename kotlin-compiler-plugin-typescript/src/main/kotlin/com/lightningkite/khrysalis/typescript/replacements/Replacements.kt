package com.lightningkite.khrysalis.typescript.replacements

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.util.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.synthetic.SamAdapterExtensionFunctionDescriptor
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor
import org.jetbrains.kotlin.types.KotlinType
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Replacements() {
    val attributes: HashMap<String, TreeSet<AttributeReplacement>> = HashMap()
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
        val actualDescriptor =
            (descriptor as? SamAdapterExtensionFunctionDescriptor)?.baseDescriptorForSynthetic ?: descriptor
        if (!alreadyChecked.add(actualDescriptor)) return null
        val result = actualDescriptor.fqNamesToCheck
            .flatMap {
                functions[it]?.asSequence() ?: sequenceOf()
            }
            .find {
                it.passes(
                    analysis = analysis,
                    call = call,
                    descriptor = actualDescriptor
                )
            } ?: (actualDescriptor as? CallableMemberDescriptor)?.allOverridden()
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
        propertyDescriptor.fqNamesToCheck
            .flatMap {
                gets[it]?.asSequence() ?: sequenceOf()
            }
            .find {
                it.passes(propertyDescriptor, receiverType)
            } ?: propertyDescriptor.overriddenDescriptors.asSequence().map { getGet(it, receiverType) }.firstOrNull()
        ?: (propertyDescriptor as? SyntheticJavaPropertyDescriptor)?.getMethod?.let {
            val accessName = it.name.asString()
            val propName = propertyDescriptor.name.asString()
            val all = sequenceOf(it) + it.allOverridden()
            all.mapNotNull {
                gets[it.simpleFqName.replace(accessName, propName)]?.find {
                    it.passes(
                        propertyDescriptor,
                        receiverType
                    )
                }
                    ?: gets[it.simplerFqName.replace(accessName, propName)]?.find {
                        it.passes(
                            propertyDescriptor,
                            receiverType
                        )
                    }
            }.firstOrNull()
        }

    fun getGet(objectDescriptor: DeclarationDescriptor): GetReplacement? =
        gets[objectDescriptor.simpleFqName]?.firstOrNull()

    fun getSet(propertyDescriptor: PropertyDescriptor, receiverType: KotlinType? = null): SetReplacement? {
        return propertyDescriptor.fqNamesToCheck
            .flatMap {
                sets[it]?.asSequence() ?: sequenceOf()
            }
            .find {
                it.passes(propertyDescriptor, receiverType)
            } ?: propertyDescriptor.overriddenDescriptors.asSequence().map { getSet(it, receiverType) }.firstOrNull()
        ?: (propertyDescriptor as? SyntheticJavaPropertyDescriptor)?.setMethod?.let {
            val accessName = it.name.asString()
            val propName = propertyDescriptor.name.asString()
            val all = sequenceOf(it) + it.allOverridden()
            all.mapNotNull {
                sets[it.simpleFqName.replace(accessName, propName)]?.find {
                    it.passes(
                        propertyDescriptor,
                        receiverType
                    )
                }
                    ?: sets[it.simplerFqName.replace(accessName, propName)]?.find {
                        it.passes(
                            propertyDescriptor,
                            receiverType
                        )
                    }
            }.firstOrNull()
        }
    }

    fun getType(type: DeclarationDescriptor): TypeReplacement? =
        type.fqNamesToCheck
            .flatMap {
                types[it]?.asSequence() ?: sequenceOf()
            }
            .find {
                it.passes(type)
            }

    fun getType(type: KotlinType): TypeReplacement? =
        if (type.constructor.declarationDescriptor is TypeParameterDescriptor) null else types[type.fqNameWithoutTypeArgs]?.find { it.passes(type) }
    fun getTypeRef(type: KotlinType): TypeRefReplacement? =
        typeRefs[type.fqNameWithoutTypeArgs]?.find { it.passes(type) }

    fun getTypeRef(type: DeclarationDescriptor): TypeRefReplacement? =
        type.fqNamesToCheck
            .flatMap {
                typeRefs[it]?.asSequence() ?: sequenceOf()
            }
            .find {
                it.passes(type)
            }

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
            is AttributeReplacement -> attributes.getOrPut(item.id) { TreeSet() }.add(item)
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


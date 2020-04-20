package com.lightningkite.khrysalis.typescript.replacements

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

class Replacements() {
    val functions: HashMap<String, ArrayList<FunctionReplacement>> = HashMap()
    val gets: HashMap<String, ArrayList<GetReplacement>> = HashMap()
    val sets: HashMap<String, ArrayList<SetReplacement>> = HashMap()
    val types: HashMap<String, ArrayList<TypeReplacement>> = HashMap()
    val typeRefs: HashMap<String, ArrayList<TypeRefReplacement>> = HashMap()

    operator fun plusAssign(item: FunctionReplacement) {
        functions.getOrPut(item.fqName) { ArrayList() }.add(item)
    }

    operator fun plusAssign(item: GetReplacement) {
        gets.getOrPut(item.fqName) { ArrayList() }.add(item)
    }

    operator fun plusAssign(item: SetReplacement) {
        sets.getOrPut(item.fqName) { ArrayList() }.add(item)
    }

    operator fun plusAssign(item: TypeReplacement) {
        types.getOrPut(item.fqName) { ArrayList() }.add(item)
    }

    operator fun plusAssign(item: TypeRefReplacement) {
        typeRefs.getOrPut(item.fqName) { ArrayList() }.add(item)
    }

    fun getCall(functionDescriptor: FunctionDescriptor): FunctionReplacement? = functions[functionDescriptor.fqNameSafe.asString()]?.find { it.passes(functionDescriptor) }
    fun getGet(propertyDescriptor: PropertyDescriptor): GetReplacement? = gets[propertyDescriptor.fqNameSafe.asString()]?.find { it.passes(propertyDescriptor) }
    fun getSet(propertyDescriptor: PropertyDescriptor): SetReplacement? = sets[propertyDescriptor.fqNameSafe.asString()]?.find { it.passes(propertyDescriptor) }
    fun getType(type: KotlinType): TypeReplacement? = types[type.getJetTypeFqName(false)]?.find { it.passes(type) }
    fun getTypeRef(type: KotlinType): TypeRefReplacement? = typeRefs[type.getJetTypeFqName(false)]?.find { it.passes(type) }
}

sealed class TemplatePart {
    class Text(val string: String) : TemplatePart()
    object DispatchReceiver : TemplatePart()
    object ExtensionReceiver : TemplatePart()
    object Value : TemplatePart()
    class ParameterReceiver(val name: String) : TemplatePart()
    class TypeParameterReceiver(val name: String) : TemplatePart()
}

class FunctionReplacement(
    val fqName: String,
    val receiverFilter: String? = null,
    val argumentFilters: List<Pair<String, String>> = listOf(),
    val template: List<TemplatePart>
) {
    fun passes(decl: FunctionDescriptor): Boolean {
        return decl.fqNameSafe.asString() == fqName &&
                (receiverFilter == null || receiverFilter == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                )) &&
                argumentFilters.all { f ->
                    decl.valueParameters.find { it.name.asString() == f.first }?.type?.getJetTypeFqName(false) == f.second
                }
    }
}

class GetReplacement(
    val fqName: String,
    val receiverFilter: String? = null,
    val template: List<TemplatePart>
) {
    fun passes(decl: PropertyDescriptor): Boolean {
        return decl.fqNameSafe.asString() == fqName &&
                (receiverFilter == null || receiverFilter == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                ))
    }
}

class SetReplacement(
    val fqName: String,
    val receiverFilter: String? = null,
    val template: List<TemplatePart>
) {
    fun passes(decl: PropertyDescriptor): Boolean {
        return decl.fqNameSafe.asString() == fqName &&
                (receiverFilter == null || receiverFilter == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                ))
    }
}

class TypeReplacement(
    val fqName: String,
    val template: List<TemplatePart>
) {
    fun passes(decl: KotlinType): Boolean {
        return decl.getJetTypeFqName(false) == fqName
    }
}

class TypeRefReplacement(
    val fqName: String,
    val template: List<TemplatePart>
) {
    fun passes(decl: KotlinType): Boolean {
        return decl.getJetTypeFqName(false) == fqName
    }
}
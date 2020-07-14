package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.*

private val primitiveTypes = setOf(
    "kotlin.Byte",
    "kotlin.Short",
    "kotlin.Int",
    "kotlin.Long",
    "kotlin.UByte",
    "kotlin.UShort",
    "kotlin.UInt",
    "kotlin.ULong",
    "kotlin.Float",
    "kotlin.Double",
    "kotlin.Char",
    "kotlin.String",
    "kotlin.Boolean",
    "kotlin.Unit",
    "kotlin.Any"
)

fun KotlinType.isPrimitive() = getJetTypeFqName(false) in primitiveTypes
data class BasicType(val type: KotlinType)
data class CompleteReflectableType(val type: KotlinType)
data class KtUserTypeBasic(val type: KtUserType)
data class SwiftExtensionStart(val forDescriptor: CallableDescriptor, val typeParams: KtTypeParameterList?)

val partOfParameterLocal = ThreadLocal<Boolean>()
var partOfParameter: Boolean
    get() = partOfParameterLocal.get() ?: false
    set(value) {
        partOfParameterLocal.set(value)
    }

fun KotlinType.worksAsSwiftConstraint(): Boolean {
    if (this.getJetTypeFqName(false) == "kotlin.Any") return false
    return when (this) {
        is WrappedType -> false
        is SimpleType -> true
        is FlexibleType -> false //could work later?
    }
}

fun CallableDescriptor.worksAsSwiftConstraint(): Boolean {
    return extensionReceiverParameter?.type?.worksAsSwiftConstraint() != false
}

fun SwiftTranslator.registerType() {

    handle<SwiftExtensionStart> {
        -"extension "
        val t = typedRule.forDescriptor.extensionReceiverParameter!!.type
        val baseClass = t.constructor.declarationDescriptor as? ClassDescriptor
        -BasicType(t)
        t.arguments
            .mapIndexedNotNull { index, arg ->
                if (arg.type.isTypeParameter()) {
                    val x = (arg.type.constructor.declarationDescriptor as? TypeParameterDescriptor)?.name?.asString()
                    val match = typedRule.typeParams?.parameters?.find { it.name == x }?.extendsBound
                        ?: return@mapIndexedNotNull null
                    val type = match
                    if (type.resolvedType?.constructor?.declarationDescriptor is TypeParameterDescriptor) return@mapIndexedNotNull null
                    val swiftExactly = type.annotations.flatMap { it.entries }.find {
                        it.typeReference?.text?.endsWith("swiftExactly") == true
                    }?.valueArguments?.let { it.firstOrNull()?.getArgumentExpression()?.text?.trim('"') ?: "T" }
                    val swiftDescendsFrom = type.annotations.flatMap { it.entries }.find {
                        it.typeReference?.text?.endsWith("swiftDescendsFrom") == true
                    }?.valueArguments?.let { it.firstOrNull()?.getArgumentExpression()?.text?.trim('"') ?: "T" }
                    when {
                        swiftExactly != null -> {
                            listOf(swiftExactly, " == ", type)
                        }
                        swiftDescendsFrom != null -> {
                            listOf(swiftDescendsFrom, " : ", type)
                        }
                        else -> {
                            val name = x ?: baseClass?.declaredTypeParameters?.get(index)?.name?.asString()
                            val c = type.resolvedType?.constructor?.declarationDescriptor as? ClassDescriptor
                            if (c?.isFinalOrEnum == false) {
                                listOf(name, ": ", type)
                            } else {
                                listOf(name, " == ", type)
                            }
                        }
                    }
                } else {
                    val type = arg.type
                    if (type.constructor.declarationDescriptor is TypeParameterDescriptor) return@mapIndexedNotNull null
                    val swiftExactly = type.annotations.find {
                        it.fqName?.asString()?.endsWith("swiftExactly") == true
                    }?.allValueArguments?.entries?.let { it.firstOrNull()?.value?.value as? String ?: "T" }
                    val swiftDescendsFrom = type.annotations.find {
                        it.fqName?.asString()?.endsWith("swiftDescendsFrom") == true
                    }?.allValueArguments?.entries?.let { it.firstOrNull()?.value?.value as? String ?: "T" }
                    when {
                        swiftExactly != null -> {
                            listOf(swiftExactly, " == ", type)
                        }
                        swiftDescendsFrom != null -> {
                            listOf(swiftDescendsFrom, ": ", type)
                        }
                        else -> {
                            val name = baseClass?.declaredTypeParameters?.get(index)?.name?.asString()
                            val c = type.constructor.declarationDescriptor as? ClassDescriptor
                            if (c?.isFinalOrEnum == false) {
                                listOf(name, ": ", type)
                            } else {
                                listOf(name, " == ", type)
                            }
                        }
                    }
                }
            }
            .takeUnless { it.isEmpty() }
            ?.let {
                -" where "
                it.forEachBetween(
                    forItem = { -it },
                    between = { -", " }
                )
            }
        -" {"
    }

    handle<KtTypeAlias> {
        -(typedRule.swiftVisibility() ?: "public")
        -" typealias "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" = "
        -typedRule.getTypeReference()
    }

    handle<KotlinType> {
        when (val desc = typedRule.constructor.declarationDescriptor) {
            is FunctionClassDescriptor -> {
                if (partOfParameter && typedRule.annotations.any { it.fqName?.asString() == "com.lightningkite.khrysalis.escaping" }) {
                    -"@escaping "
                }
                -'('
                typedRule.arguments.dropLast(1).forEachBetween(
                    forItem = { typeProjection ->
                        -typeProjection
                    },
                    between = { -", " }
                )
                -") -> "
                -typedRule.arguments.last()
            }
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = arrayListOf(current)
                while (true) {
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                items.asReversed().forEachBetween(
                    forItem = { -it.name.asString() },
                    between = { -'.' }
                )
                typedRule.arguments.takeUnless { it.isEmpty() }?.let {
                    -'<'
                    it.forEachBetween(
                        forItem = { -it },
                        between = { -", " }
                    )
                    -'>'
                }
            }
            is TypeParameterDescriptor -> {
                -desc.name.asString()
            }
            else -> {
                println("What is this? ${desc?.let { it::class.qualifiedName }}")
            }
        }
        if (typedRule.isMarkedNullable) {
            -'?'
        }
    }

    handle<BasicType> {
        when (val desc = typedRule.type.constructor.declarationDescriptor) {
            null -> {
                -"/*null type*/any"
            }
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = arrayListOf(current)
                while (true) {
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                items.asReversed().forEachBetween(
                    forItem = { -it.name.asString() },
                    between = { -'.' }
                )
            }
            is TypeParameterDescriptor -> {
                -desc.name.asString()
            }
            else -> {
                -"/*${desc::class.java}*/"
            }
        }
    }

    handle<CompleteReflectableType> {
        -"["
        -BasicType(typedRule.type)
        typedRule.type.arguments.forEach {
            -", "
            -CompleteReflectableType(it.type)
        }
        -"]"
    }

    handle<KtUserType>(
        condition = {
            val reference = typedRule.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget ?: return@handle false
            replacements.getType(type) != null
        },
        priority = 10_000,
        action = {
            val reference = typedRule.referenceExpression!!
            val type = reference.resolvedReferenceTarget!!
            val rule = replacements.getType(type)!!
            val declaredParams = when (type) {
                is ClassDescriptor -> type.declaredTypeParameters
                is TypeAliasDescriptor -> type.declaredTypeParameters
                else -> listOf()
            }
            val typeParametersByName = typedRule.typeArguments.withIndex()
                .associate { (index, item) -> declaredParams[index].name.asString() to item }
            emitTemplate(
                template = rule.template,
                typeParameter = { typeParametersByName[it.name] ?: "undefined" },
                typeParameterByIndex = { typedRule.typeArguments.getOrNull(it.index) ?: "undefined" }
            )
        }
    )

    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget ?: return@handle false
            replacements.getTypeRef(type) != null
        },
        priority = 11_000,
        action = {
            val reference = typedRule.type.referenceExpression!!
            val type = reference.resolvedReferenceTarget!!
            val rule = replacements.getTypeRef(type)!!
            emitTemplate(
                template = rule.template
            )
        }
    )
    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget ?: return@handle false
            replacements.getType(type) != null
        },
        priority = 10_000,
        action = {
            val reference = typedRule.type.referenceExpression!!
            val type = reference.resolvedReferenceTarget!!
            val rule = replacements.getType(type)!!
            emitTemplate(
                template = rule.template
            )
        }
    )
    handle<KtUserTypeBasic> {
        -typedRule.type.referenceExpression
    }

    handle<KtFunctionType> {
        -"("
        listOfNotNull(typedRule.receiverTypeReference?.let { null to it }).plus(typedRule.parameters.map { it.nameIdentifier to it.typeReference })
            .forEachBetween(
                forItem = {
                    -it.second
                },
                between = { -", " }
            )
        -") -> "
        -typedRule.returnTypeReference
    }

    handle<KtTypeReference>(
        condition = {
            partOfParameter && typedRule.annotationEntries
                .any { it.resolvedAnnotation?.fqName?.asString() == "com.lightningkite.khrysalis.escaping" }
                    && typedRule.parentOfType<KtParameter>() != null
        },
        priority = 10,
        action = {
            -"@escaping "
            doSuper()
        }
    )

    handle<KtTypeProjection> {
        when (typedRule.projectionKind) {
            KtProjectionKind.IN -> -typedRule.typeReference
            KtProjectionKind.OUT -> -typedRule.typeReference
            KtProjectionKind.STAR -> -"Any?"
            KtProjectionKind.NONE -> -typedRule.typeReference
        }
    }

    handle<KtTypeParameter> {
        -typedRule.nameIdentifier
        typedRule.extendsBound?.let {
            -" : "
            -it
        }
    }

    handle<TypeProjectionBase> {
        -typedRule.type
    }

    handle<KotlinType>(
        condition = {
            replacements.getType(typedRule) != null
        },
        priority = 10_000,
        action = {
            val type = typedRule
            val rule = replacements.getType(type)!!
            val baseType = type.constructor
            val typeParametersByName = type.arguments.withIndex()
                .associate { (index, item) -> baseType.parameters[index].name.asString() to item }
            emitTemplate(
                template = rule.template,
                typeParameter = { typeParametersByName[it.name] ?: "undefined" },
                typeParameterByIndex = { type.arguments.getOrNull(it.index) ?: "undefined" }
            )
            if(type.isMarkedNullable){
                -'?'
            }
        }
    )

    handle<BasicType>(
        condition = {
            replacements.getTypeRef(typedRule.type) != null
        },
        priority = 10_000,
        action = {
            val type = typedRule.type
            val rule = replacements.getTypeRef(type)!!
            val baseType = type.constructor
            emitTemplate(
                template = rule.template
            )
        }
    )
    handle<BasicType>(
        condition = {
            replacements.getType(typedRule.type) != null
        },
        priority = 9_000,
        action = {
            val type = typedRule.type
            val rule = replacements.getType(type)!!
            val baseType = type.constructor
            emitTemplate(
                template = rule.template
            )
        }
    )

    handle<KtIsExpression> {
        if (typedRule.isNegated) {
            -"!("
        }
        -typedRule.leftHandSide
        -" is "
        -typedRule.typeReference
        if (typedRule.isNegated) {
            -")"
        }
    }

    handle<KtBinaryExpressionWithTypeRHS>(
        condition = { typedRule.operationReference.getReferencedNameElementType() == KtTokens.AS_SAFE },
        priority = 100,
        action = {
            -typedRule.left
            -" as? "
            -typedRule.right
        }
    )
    handle<KtBinaryExpressionWithTypeRHS>(
        condition = { typedRule.operationReference.getReferencedNameElementType() == KtTokens.AS_KEYWORD },
        priority = 100,
        action = {
            -typedRule.left
            -" as! "
            -typedRule.right
        }
    )
}
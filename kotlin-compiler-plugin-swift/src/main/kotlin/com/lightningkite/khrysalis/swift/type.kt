package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.*
import java.util.*

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
data class SwiftExtensionStart(val forDescriptor: CallableDescriptor)

private val partOfParameterWeak = WeakHashMap<KotlinType, Boolean>()
var KotlinType.partOfParameter: Boolean
    get() = partOfParameterWeak[this] ?: false
    set(value){
        partOfParameterWeak[this] = value
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
                if (arg.type.constructor.declarationDescriptor is TypeParameterDescriptor) return@mapIndexedNotNull null
                val swiftExactly = arg.type.annotations.find {
                    it.fqName?.asString()?.endsWith("swiftExactly") == true
                }?.allValueArguments?.entries?.let { it.firstOrNull()?.value?.value as? String ?: "T" }
                val swiftDescendsFrom = arg.type.annotations.find {
                    it.fqName?.asString()?.endsWith("swiftDescendsFrom") == true
                }?.allValueArguments?.entries?.let { it.firstOrNull()?.value?.value as? String ?: "T" }
                when {
                    swiftExactly != null -> {
                        listOf(swiftExactly, ": ", arg.type)
                    }
                    swiftDescendsFrom != null -> {
                        listOf(swiftDescendsFrom, " == ", arg.type)
                    }
                    else -> {
                        val name = baseClass?.declaredTypeParameters?.get(index)?.name?.asString()
                        val c = arg.type.constructor.declarationDescriptor as? ClassDescriptor
                        if (c?.isFinalOrEnum == false) {
                            listOf(name, ": ", arg.type)
                        } else {
                            listOf(name, " == ", arg.type)
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
        -(typedRule.visibilityModifier() ?: "public")
        -" typealias "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" = "
        -typedRule.getTypeReference()
    }

    handle<KotlinType> {
        when (val desc = typedRule.constructor.declarationDescriptor) {
            is FunctionClassDescriptor -> {
                if (typedRule.partOfParameter && typedRule.annotations.any { it.fqName?.asString() == "com.lightningkite.khrysalis.escaping" }) {
                    -"@escaping "
                }
                -'('
                typedRule.arguments.dropLast(1).forEachIndexed { index, typeProjection ->
                    -typeProjection
                }
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
            typedRule.annotationEntries
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
            KtProjectionKind.STAR -> -"any"
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
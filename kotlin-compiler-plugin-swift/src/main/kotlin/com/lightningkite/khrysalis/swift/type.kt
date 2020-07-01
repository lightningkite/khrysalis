package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
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
data class SwiftExtensionStart(val forDescriptor: CallableDescriptor)

fun KotlinType.worksAsSwiftConstraint(): Boolean {
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
            .mapIndexedNotNull { index, it ->
                if(it.type.constructor.declarationDescriptor is TypeParameterDescriptor) return@mapIndexedNotNull null
                val swiftExactly = it.type.annotations.find {
                    it.fqName?.asString()?.endsWith("swiftExactly") == true
                }?.allValueArguments?.entries?.first()?.value?.value as? String
                val swiftDescendsFrom = it.type.annotations.find {
                    it.fqName?.asString()?.endsWith("swiftDescendsFrom") == true
                }?.allValueArguments?.entries?.first()?.value?.value as? String
                when {
                    swiftExactly != null -> {
                        listOf(swiftExactly, ": ", it.type)
                    }
                    swiftDescendsFrom != null -> {
                        listOf(swiftDescendsFrom, " == ", it.type)
                    }
                    else -> {
                        val name = baseClass?.declaredTypeParameters?.get(index)?.name?.asString()
                        val c = it.type.constructor.declarationDescriptor as? ClassDescriptor
                        if (c?.isFinalOrEnum == false) {
                            listOf(name, ": ", it.type)
                        } else {
                            listOf(name, " == ", it.type)
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
        -"typealias "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" = "
        -typedRule.getTypeReference()
    }

    handle<KotlinType> {
        when (val desc = typedRule.constructor.declarationDescriptor) {
            is FunctionClassDescriptor -> {
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
                println("What is this? ${desc?.let { it::class.qualifiedName}}")
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

    handle<KtNullableType> {
        -"("
        -typedRule.innerType
        -" | null)"
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
        var currentNameChar = 'a'
        listOfNotNull(typedRule.receiverTypeReference?.let { null to it }).plus(typedRule.parameters.map { it.nameIdentifier to it.typeReference })
            .forEachBetween(
                forItem = {
                    var nextChar = currentNameChar++
                    while (typedRule.parameters.any {
                            val n = it.name ?: return@any false
                            n.length == 1 && n.first() == nextChar
                        }) {
                        nextChar = currentNameChar++
                    }
                    -(it.first ?: nextChar.toString())
                    -": "
                    -it.second
                },
                between = { -", " }
            )
        -") => "
        -typedRule.returnTypeReference
    }

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
            -" extends "
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
        val resolvedType = typedRule.typeReference!!.resolvedType!!
        if (typedRule.isNegated) {
            -"!("
        }
        emitIsExpression(typedRule.leftHandSide, resolvedType)
        if (typedRule.isNegated) {
            -")"
        }
    }

    handle<KtBinaryExpressionWithTypeRHS>(
        condition = { typedRule.operationReference.getReferencedNameElementType() == KtTokens.AS_SAFE },
        priority = 100,
        action = {
            val resolvedType = typedRule.right!!.resolvedType!!

            when {
                resolvedType.isInterface() -> {
                    -"tryCastInterface<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", \""
                    -resolvedType.getJetTypeFqName(false).split('.').joinToString("") { it.capitalize() }
                    -"\")"
                }
                resolvedType.isPrimitive() -> {
                    -"tryCastPrimitive<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", \""
                    -resolvedType
                    -"\")"
                }
                else -> {
                    -"tryCastClass<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", "
                    -BasicType(resolvedType)
                    -")"
                }
            }
        }
    )
}

fun PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<*>.emitIsExpression(
    expression: Any?,
    resolvedType: KotlinType
) {
    when {
        resolvedType.isInterface() -> {
            -"checkIsInterface<"
            -resolvedType
            -">("
            -expression
            -", \""
            -resolvedType.getJetTypeFqName(false).split('.').joinToString("") { it.capitalize() }
            -"\")"
        }
        resolvedType.isPrimitive() -> {
            -"typeof ("
            -expression
            -") == \""
            -resolvedType
            -'"'
        }
        else -> {
            -expression
            -" instanceof "
            -BasicType(resolvedType)
        }
    }
}

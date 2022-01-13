package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.checker.NewCapturedTypeConstructor
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

fun KotlinType.isPrimitive() = fqNameWithoutTypeArgs in primitiveTypes
data class BasicType(val type: KotlinType)
data class CompleteReflectableType(val type: KotlinType)
data class KtUserTypeBasic(val type: KtSimpleNameExpression)
data class SwiftExtensionStart(
    val forDescriptor: CallableDescriptor,
    val receiver: KtTypeReference?,
    val typeParams: KtTypeParameterList?
)

val partOfParameterLocal = ThreadLocal<Int>()
var writingParameter: Int
    get() = partOfParameterLocal.get() ?: 0
    set(value) {
        partOfParameterLocal.set(value)
    }

fun KotlinType.worksAsSwiftConstraint(): Boolean {
    if (this.fqNameWithoutTypeArgs == "kotlin.Any") return false
    (constructor.declarationDescriptor as? TypeParameterDescriptor)?.upperBounds?.let {
        if(it.all { it.fqNameWithoutTypeArgs == "kotlin.Any" }) return false
    }
    return when (this) {
        is WrappedType -> false
        is SimpleType -> true
        is FlexibleType -> false //could work later?
    } && this.arguments.all { it.type.arguments.isEmpty() && !(it.type.constructor.declarationDescriptor is TypeParameterDescriptor && it.type.isMarkedNullable) }
}

fun CallableDescriptor.worksAsSwiftConstraint(): Boolean {
    return extensionReceiverParameter?.type?.worksAsSwiftConstraint() != false
}

val knownTypeParameterNames = mapOf(
    "kotlin.sequences.Sequence" to listOf("Element"),
    "kotlin.collections.Iterable" to listOf("Element"),
    "kotlin.collections.Collection" to listOf("Element"),
    "kotlin.collections.List" to listOf("Element"),
    "kotlin.Array" to listOf("Element"),
    "kotlin.collections.Map" to listOf("Key", "Value")
)


fun SwiftTranslator.registerType() {
    fun ClassDescriptor.useExactEqualForGeneric(): Boolean {
        return isFinalOrEnum || when(this.fqNameOrNull()?.asString()) {
            "kotlin.Array",
            "kotlin.collections.Map",
            "kotlin.collections.Set",
            "kotlin.collections.List" -> true
            else -> false
        }
    }

    handle<SwiftExtensionStart> {
        -"extension "
        val replacement = typedRule.forDescriptor.extensionReceiverParameter?.type?.let { replacements.getType(it) }
        val t = typedRule.forDescriptor.extensionReceiverParameter!!.type
        var whereEmitted = false
        replacement?.constraintTemplate?.let {
            emitTemplate(it)
            whereEmitted = it.parts.joinToString("").contains(" where ")
        } ?: run {
            -typedRule.receiver?.typeElement?.let { it as? KtUserType }?.let { KtUserTypeBasic(it.referenceExpression!!) }
                ?: BasicType(t)
        }
        t.arguments
            .mapIndexedNotNull { index, arg ->
                if(arg.isStarProjection) return@mapIndexedNotNull null
                if(arg.type.isTypeParameter() && (arg.type.constructor.declarationDescriptor as? TypeParameterDescriptor)?.upperBounds?.all { it.isAnyOrNullableAny() } == true) return@mapIndexedNotNull null
                val name = replacement?.typeArgumentNames?.get(index)
                    ?: knownTypeParameterNames[t.constructor.declarationDescriptor?.fqNameOrNull()?.asString()]?.get(index)
                    ?: "Element"
                val type = if(arg.type.isTypeParameter())
                    typedRule.typeParams?.parameters
                        ?.find { it.name == (arg.type.constructor.declarationDescriptor as? TypeParameterDescriptor)?.name?.asString() }
                        ?.extendsBound?.resolvedType
                        ?: return@mapIndexedNotNull null
                else
                    arg.type
                val useExtends = (type.constructor.declarationDescriptor as? ClassDescriptor)?.useExactEqualForGeneric() ?: false
                if(useExtends)
                    return@mapIndexedNotNull listOf(name, " : ", type)
                else
                    return@mapIndexedNotNull listOf(name, " == ", type)
            }
            .takeUnless { it.isEmpty() }
            ?.let {
                if(whereEmitted) {
                    -", "
                } else {
                    -" where "
                }
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
                if (typedRule.isMarkedNullable) {
                    -'('
                } else if (writingParameter > 0) {
                    -"@escaping "
                }
                -'('
                writingParameter++
                typedRule.arguments.dropLast(1).forEachBetween(
                    forItem = { typeProjection ->
                        -typeProjection
                    },
                    between = { -", " }
                )
                writingParameter--
                -") -> "
                -typedRule.arguments.last()
                if (typedRule.isMarkedNullable) {
                    -')'
                }
            }
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = arrayListOf(current)
                while (!current.swiftTopLevelMessedUp) {
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                items.asReversed().forEachBetween(
                    forItem = { -it.swiftTopLevelName },
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
                typeParameter = { typeParametersByName[it.name] ?: "nil" },
                typeParameterByIndex = { typedRule.typeArguments.getOrNull(it.index) ?: "nil" }
            )
        }
    )

    handle<KtUserType>(
        condition = {
            val reference = typedRule.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget ?: return@handle false
            type is ClassDescriptor && type.swiftTopLevelMessedUp
        },
        priority = 100,
        action = {
            val reference = typedRule.referenceExpression!!
            val type = reference.resolvedReferenceTarget as ClassDescriptor
            -type.swiftTopLevelName
            -typedRule.typeArgumentList
        }
    )

    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type ?: return@handle false
            val type = reference.resolvedReferenceTarget ?: return@handle false
            replacements.getTypeRef(type) != null
        },
        priority = 11_000,
        action = {
            val reference = typedRule.type
            val type = reference.resolvedReferenceTarget!!
            val rule = replacements.getTypeRef(type)!!
            emitTemplate(
                template = rule.template
            )
        }
    )
    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type ?: return@handle false
            val type = reference.resolvedReferenceTarget ?: return@handle false
            replacements.getType(type) != null
        },
        priority = 10_000,
        action = {
            val reference = typedRule.type
            val type = reference.resolvedReferenceTarget!!
            val rule = replacements.getType(type)!!
            emitTemplate(
                template = rule.template
            )
        }
    )
    handle<KtUserTypeBasic> {
        -typedRule.type
    }

    handle<KtFunctionType> {
        -"("
        writingParameter++
        listOfNotNull(typedRule.receiverTypeReference?.let { null to it }).plus(typedRule.parameters.map { it.nameIdentifier to it.typeReference })
            .forEachBetween(
                forItem = {
                    -it.second
                },
                between = { -", " }
            )
        writingParameter--
        -") -> "
        -typedRule.returnTypeReference
    }

    handle<KtTypeReference>(
        condition = {
            val type = typedRule.resolvedType ?: return@handle false
            val replacement = (typedRule.typeElement as? KtUserType)?.referenceExpression?.resolvedReferenceTarget?.let { replacements.getType(it) } ?: replacements.getType(type)
            writingParameter > 0
                    && !type.isMarkedNullable
                    && (type.isFunctionType || replacement?.isFunctionType == true)
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
            if(rule.isFunctionType && writingParameter > 0 && !type.isMarkedNullable) {
                -"@escaping "
            }
            emitTemplate(
                template = rule.template,
                typeParameter = { typeParametersByName[it.name] ?: "nil" },
                typeParameterByIndex = { type.arguments.getOrNull(it.index) ?: "nil" }
            )
            if (type.isMarkedNullable) {
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
        typedRule.typeReference?.resolvedType?.let { replacements.getType(it) }?.errorCondition?.let {
            -" && "
            emitTemplate(it, receiver = typedRule.leftHandSide)
        }
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
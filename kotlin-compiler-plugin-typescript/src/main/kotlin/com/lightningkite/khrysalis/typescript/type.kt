package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.replacements.Template
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.leastPermissiveDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.*
import kotlin.text.Appendable
import com.lightningkite.khrysalis.analysis.*

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
data class KtUserTypeBasic(val type: KtUserType)

fun TypescriptTranslator.registerType() {

    handle<KtTypeAlias> {
        if (typedRule.isTopLevel() && !typedRule.isPrivate()) {
            -"$declaresPrefix${typedRule.fqName?.asString()}\n"
            -"export "
        }
        -"type "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" = "
        -typedRule.getTypeReference()
        -";\n"

        (typedRule.getTypeReference()?.typeElement as? KtUserType)?.let { ut ->
            if (typedRule.visibilityModifierTypeOrDefault().value == "public") {
                -"$declaresPrefix${typedRule.fqName?.asString()}\n"
                -"export "
            }
            -"let "
            -typedRule.nameIdentifier
            -" = "
            -KtUserTypeBasic(ut)
            -";\n"
        }
    }

    handle<KotlinType> {
        if(typedRule.isMarkedNullable) {
            -"("
        }
        when (val desc = typedRule.constructor.declarationDescriptor) {
            is FunctionClassDescriptor -> {
                if(typedRule.isMarkedNullable) -"("
                -'('
                typedRule.arguments.dropLast(1).withIndex().forEachBetween(
                    forItem = { (index, typeProjection) ->
                        -('a' + index)
                        -": "
                        -typeProjection
                    },
                    between = { -", " }
                )
                -") => "
                -typedRule.arguments.last()
                if(typedRule.isMarkedNullable) -")"
            }
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = arrayListOf(current)
                while (true) {
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                out.addImport(current)
                items.asReversed().forEachBetween(
                    forItem = { -it.name.asString().safeJsIdentifier() },
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
                -desc.name.asString().safeJsIdentifier()
            }
            else -> {
                println("What is this? ${desc?.let { it::class.qualifiedName }}")
            }
        }
        if(typedRule.isMarkedNullable) {
            -" | null)"
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
                out.addImport(current)
                items.asReversed().forEachBetween(
                    forItem = { -it.name.asString().safeJsIdentifier() },
                    between = { -'.' }
                )
            }
            is TypeParameterDescriptor -> {
                -desc.name.asString().safeJsIdentifier()
            }
            else -> {
                -"/*${desc::class.java}*/"
            }
        }
    }

    handle<CompleteReflectableType> {
        val desc = typedRule.type.constructor.declarationDescriptor
        if(desc is TypeParameterDescriptor) {
            -desc.name.asString().safeJsIdentifier()
        } else {
            -"["
            -BasicType(typedRule.type)
            typedRule.type.arguments.forEach {
                -", "
                -CompleteReflectableType(it.type)
            }
            -"]"
        }
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
        -"(("
        var currentNameChar = 'a'
        listOfNotNull(typedRule.receiverTypeReference?.let { null to it }).plus(typedRule.parameters.map { it.nameIdentifier to it.typeReference }).forEachBetween(
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
        -")"
    }

    handle<KtTypeProjection> {
        when(typedRule.projectionKind){
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
            if(typedRule.isMarkedNullable) {
                -"("
            }
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
            if(typedRule.isMarkedNullable) {
                -" | null)"
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
            val parts = ArrayList<TemplatePart>()
            label@for(part in rule.template.parts) {
                when(part){
                    is TemplatePart.Text -> {
                        if(part.string.contains('<')) {
                            parts.add(TemplatePart.Text(part.string.substringBefore('<')))
                            break@label
                        } else {
                            parts.add(part)
                        }
                    }
                    else -> parts.add(part)
                }
            }
            emitTemplate(
                template = rule.template.copy(parts=parts)
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
                    out.addImport("butterfly-web/dist/Kotlin", "tryCastInterface")
                    -"tryCastInterface<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", \""
                    -resolvedType.fqNameWithoutTypeArgs.split('.').joinToString("") { it.capitalize() }
                    -"\")"
                }
                resolvedType.isPrimitive() -> {
                    out.addImport("butterfly-web/dist/Kotlin", "tryCastPrimitive")
                    -"tryCastPrimitive<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", \""
                    -resolvedType
                    -"\")"
                }
                else -> {
                    out.addImport("butterfly-web/dist/Kotlin", "tryCastClass")
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

fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.emitIsExpression(
    expression: Any?,
    resolvedType: KotlinType
) {
    when {
        resolvedType.isInterface() -> {
            out.addImport("butterfly-web/dist/Kotlin", "checkIsInterface")
            -"checkIsInterface<"
            -resolvedType
            -">("
            -expression
            -", \""
            -resolvedType.fqNameWithoutTypeArgs.split('.').joinToString("") { it.capitalize() }
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

package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
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
    "kotlin.String",
    "kotlin.Boolean",
    "kotlin.Unit",
    "kotlin.Any"
)

fun KotlinType.isPrimitive() = getJetTypeFqName(false) in primitiveTypes
data class BasicType(val type: KotlinType)
data class KtUserTypeBasic(val type: KtUserType)

fun TypescriptTranslator.registerType() {

    handle<KtTypeAlias> {
        if (typedRule.isTopLevel() && !typedRule.isPrivate()) -"export "
        -"type "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" = "
        -typedRule.getTypeReference()
        -";\n"

        (typedRule.getTypeReference()?.typeElement as? KtUserType)?.let { ut ->
            if (typedRule.visibilityModifierTypeOrDefault().value == "public") {
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
        when(val desc = typedRule.constructor.declarationDescriptor) {
            is FunctionClassDescriptor -> {
                -'('
                typedRule.arguments.dropLast(1).forEachIndexed { index, typeProjection ->
                    -('a' + index)
                    -": "
                    -typeProjection
                }
                -") => "
                -typedRule.arguments.last()
            }
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = ArrayList<ClassDescriptor>()
                while(!current.tsTopLevelMessedUp){
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                out.addImport(current, current.tsTopLevelName)
                -current.tsTopLevelName
                for(item in items.asReversed()){
                    -'.'
                    -item.name.asString()
                }
                typedRule.arguments.takeUnless { it.isEmpty() }?.let {
                    -'<'
                    -it
                    -'>'
                }
            }
        }
    }

    handle<BasicType> {
        when(val desc = typedRule.type.constructor.declarationDescriptor) {
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = ArrayList<ClassDescriptor>()
                while (!current.tsTopLevelMessedUp) {
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                out.addImport(current, current.tsTopLevelName)
                -current.tsTopLevelName
                for (item in items.asReversed()) {
                    -'.'
                    -item.name.asString()
                }
            }
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
            val type = reference.resolvedReferenceTarget as? ClassDescriptor ?: return@handle false
            replacements.getType(type) != null
        },
        priority = 10_000,
        action = {
            val reference = typedRule.referenceExpression!!
            val type = reference.resolvedReferenceTarget as ClassDescriptor
            val rule = replacements.getType(type)!!
            val typeParametersByName = typedRule.typeArguments.withIndex()
                .associate { (index, item) -> type.declaredTypeParameters[index].name.asString() to item }
            rule.template.parts.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    is TemplatePart.TypeParameter -> -(typeParametersByName[part.name])
                    is TemplatePart.TypeParameterByIndex -> -(typedRule.typeArguments.getOrNull(part.index))
                }
            }
        }
    )

    handle<KtUserType>(
        condition = {
            val reference = typedRule.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget as? ClassDescriptor ?: return@handle false
            type.tsTopLevelMessedUp
        },
        priority = 100,
        action = {
            val reference = typedRule.referenceExpression!!
            val type = reference.resolvedReferenceTarget as ClassDescriptor
            val n = type.tsTopLevelName
            -n
            -typedRule.typeArgumentList
            out.addImport(type, n)
        }
    )

    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget as? ClassDescriptor ?: return@handle false
            replacements.getTypeRef(type) != null
        },
        priority = 11_000,
        action = {
            val reference = typedRule.type.referenceExpression!!
            val type = reference.resolvedReferenceTarget as ClassDescriptor
            val rule = replacements.getTypeRef(type)!!
            rule.template.parts.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                }
            }
        }
    )
    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget as? ClassDescriptor ?: return@handle false
            replacements.getType(type) != null
        },
        priority = 10_000,
        action = {
            val reference = typedRule.type.referenceExpression!!
            val type = reference.resolvedReferenceTarget as ClassDescriptor
            val rule = replacements.getType(type)!!
            rule.template.parts.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                }
            }
        }
    )
    handle<KtUserTypeBasic>(
        condition = {
            val reference = typedRule.type.referenceExpression ?: return@handle false
            val type = reference.resolvedReferenceTarget as? ClassDescriptor ?: return@handle false
            type.tsTopLevelMessedUp
        },
        priority = 100,
        action = {
            val reference = typedRule.type.referenceExpression!!
            val type = reference.resolvedReferenceTarget as ClassDescriptor
            val n = type.tsTopLevelName
            -n
            out.addImport(type, n)
        }
    )
    handle<KtUserTypeBasic>{
        -typedRule.type.referenceExpression
    }

    handle<KtFunctionType> {
        -"("
        var currentNameChar = 'a'
        typedRule.parameters.forEach {
            var nextChar = currentNameChar++
            while (typedRule.parameters.any {
                    val n = it.name ?: return@any false
                    n.length == 1 && n.first() == nextChar
                }) {
                nextChar = currentNameChar++
            }
            -(it.nameIdentifier ?: nextChar.toString())
            -": "
            -it.typeReference
        }
        -") => "
        -typedRule.returnTypeReference
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
            rule.template.parts.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    is TemplatePart.TypeParameter -> -(typeParametersByName[part.name])
                    is TemplatePart.TypeParameterByIndex -> -(type.arguments.getOrNull(part.index))
                }
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
            val rule = replacements.getType(type)!!
            val baseType = type.constructor
            val typeParametersByName = type.arguments.withIndex()
                .associate { (index, item) -> baseType.parameters[index].name.asString() to item }
            rule.template.parts.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                }
            }
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
            -"((): "
            -typedRule.right
            -" | null => const _item = "
            -typedRule.left
            -"if("
            emitIsExpression("_item", typedRule.right!!.resolvedType!!)
            -") { return _item as "
            -typedRule.right
            -"; } else { return null; }"
        }
    )
}

fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.emitIsExpression(
    expression: Any?,
    resolvedType: KotlinType
) {
    when {
        resolvedType.isInterface() -> {
            -'('
            -expression
            -".constructor as any).implementsInterface"
            -resolvedType.getJetTypeFqName(false).split('.').joinToString("") { it.capitalize() }
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
            -" instanceOf "
            -BasicType(resolvedType)
        }
    }
}
